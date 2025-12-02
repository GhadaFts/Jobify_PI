# Bookmark Feature Implementation - Mobile Kotlin

## Overview
The bookmark (favorite) feature allows recruiters to mark job offers as favorites. When a job is bookmarked, **all applications for that job** are marked as favorites.

## Architecture

### Backend Integration
- **API Service**: `BookmarkApiService.kt`
  - Endpoints for creating, deleting, and listing bookmarks
  - Communicates with `application-service` backend
  - Uses JWT authentication via ApiClient

### Data Layer
- **Model**: `Bookmark.kt` - Data class matching backend DTO
- **Repository**: `BookmarkRepository.kt`
  - Singleton pattern for global state management
  - Uses Kotlin Flow (`StateFlow`) for reactive updates
  - Caches bookmarked job offer IDs in memory
  - Methods:
    - `loadBookmarks()` - Load all bookmarks from backend
    - `addBookmark(jobOfferId)` - Add bookmark
    - `removeBookmark(jobOfferId)` - Remove bookmark
    - `toggleBookmark(jobOfferId)` - Toggle bookmark status
    - `isBookmarked(jobOfferId)` - Check if job is bookmarked

### UI Layer

#### PostsScreen.kt
- Initializes bookmark repository on screen load
- Calls `loadBookmarks()` once when composed

#### JobCard.kt
- Collects `bookmarkedJobIds` from repository using `collectAsState()`
- Determines `isJobBookmarked` by checking if job ID is in the set
- When favorite button clicked:
  - Calls `bookmarkRepository.toggleBookmark(jobId)`
  - Shows toast message on success/failure
  - State updates automatically via Flow
- Passes `favoriteApplicants` to `ApplicantsSection`:
  - If job is bookmarked → all applicant IDs
  - If not bookmarked → empty set

#### PostComponents.kt (ApplicantsSection)
- Already has "Favorites" filter button
- Filters applicants based on `favoriteApplicants` set
- Star icon shows filled (gold) or outlined (gray) based on favorite status

## Key Design Decisions

### Per-Job Bookmarks (Not Per-Application)
Following the Angular web implementation:
- Bookmarks are stored **per jobOfferId**, not per individual application
- When a job is bookmarked, **all its applications** inherit the favorite status
- This matches backend schema: `Bookmark { jobSeekerId, jobOfferId }`

### Reactive State Management
- Uses Kotlin `StateFlow` for reactive updates
- UI automatically re-renders when bookmark state changes
- No manual state synchronization needed

### Singleton Repository
- Single source of truth for bookmark state
- Shared across all JobCard instances
- Prevents duplicate network calls

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/application-service/api/bookmarks` | Create bookmark |
| GET | `/application-service/api/bookmarks/my-bookmarks` | Get all bookmarks |
| GET | `/application-service/api/bookmarks/check/{jobOfferId}` | Check if bookmarked |
| DELETE | `/application-service/api/bookmarks/job/{jobOfferId}` | Delete bookmark |

## Backend Requirements
- User must be authenticated (JWT token)
- Backend allows both `JOB_SEEKER` and `RECRUITER` roles to manage bookmarks
- Returns 204 No Content on successful deletion

## Testing Checklist
- [ ] Bookmark a job → Star icon turns gold on all applicants
- [ ] Unbookmark a job → Star icon turns gray
- [ ] Click "Favorites" filter → Shows only bookmarked job's applicants
- [ ] Bookmark persists after app restart
- [ ] Multiple jobs can be bookmarked independently
- [ ] Toast messages show on bookmark toggle
- [ ] Works with slow network (loading states)
- [ ] Handles errors gracefully (shows toast)

## Future Enhancements
- Offline bookmark support with local database
- Sync bookmarks across devices
- Bookmark count badge in navigation
- Dedicated "Favorites" screen
