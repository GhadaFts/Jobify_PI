import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface Bookmark {
  id: string;
  jobSeekerId: string;
  jobOfferId: number;
  createdAt: string;
}

export interface BookmarkRequest {
  jobOfferId: number;
}

@Injectable({
  providedIn: 'root'
})
export class BookmarkService {
  // Update this URL based on your gateway configuration
  // This points to Application Service bookmark endpoints
  private apiUrl = 'http://localhost:8888/application-service/api/bookmarks';
  
  // Cache bookmarks for current user
  private bookmarksSubject = new BehaviorSubject<Set<number>>(new Set());
  public bookmarks$ = this.bookmarksSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadBookmarks();
  }

  /**
   * Load all bookmarks for current user
   */
  loadBookmarks(): void {
    this.getMyBookmarks().subscribe({
      next: (bookmarks) => {
        const jobOfferIds = new Set(bookmarks.map(b => b.jobOfferId));
        this.bookmarksSubject.next(jobOfferIds);
      },
      error: (error) => {
        console.error('Failed to load bookmarks:', error);
      }
    });
  }

  /**
   * Get all bookmarks for current user
   */
  getMyBookmarks(): Observable<Bookmark[]> {
    return this.http.get<Bookmark[]>(`${this.apiUrl}/my-bookmarks`);
  }

  /**
   * Add a bookmark
   */
  addBookmark(jobOfferId: number): Observable<Bookmark> {
    const request: BookmarkRequest = { jobOfferId };
    
    return this.http.post<Bookmark>(this.apiUrl, request).pipe(
      tap((bookmark) => {
        // Update local cache
        const current = this.bookmarksSubject.value;
        current.add(jobOfferId);
        this.bookmarksSubject.next(new Set(current));
      })
    );
  }

  /**
   * Remove a bookmark
   */
  removeBookmark(jobOfferId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/job/${jobOfferId}`).pipe(
      tap(() => {
        // Update local cache
        const current = this.bookmarksSubject.value;
        current.delete(jobOfferId);
        this.bookmarksSubject.next(new Set(current));
      })
    );
  }

  /**
   * Toggle bookmark (add if not exists, remove if exists)
   */
  toggleBookmark(jobOfferId: number): Observable<any> {
    const isBookmarked = this.bookmarksSubject.value.has(jobOfferId);
    
    if (isBookmarked) {
      return this.removeBookmark(jobOfferId);
    } else {
      return this.addBookmark(jobOfferId);
    }
  }

  /**
   * Check if a job is bookmarked
   */
  isBookmarked(jobOfferId: number): boolean {
    return this.bookmarksSubject.value.has(jobOfferId);
  }

  /**
   * Get current bookmarks as array of job IDs
   */
  getBookmarkedJobIds(): number[] {
    return Array.from(this.bookmarksSubject.value);
  }
}

// TEMPORARY: Local storage implementation until backend is ready
@Injectable({
  providedIn: 'root'
})
export class LocalBookmarkService {
  private storageKey = 'user_bookmarks';
  private bookmarksSubject = new BehaviorSubject<Set<number>>(new Set());
  public bookmarks$ = this.bookmarksSubject.asObservable();

  constructor() {
    this.loadFromStorage();
  }

  private loadFromStorage(): void {
    const stored = localStorage.getItem(this.storageKey);
    if (stored) {
      try {
        const bookmarks = JSON.parse(stored) as number[];
        this.bookmarksSubject.next(new Set(bookmarks));
      } catch (e) {
        console.error('Failed to parse bookmarks from storage:', e);
      }
    }
  }

  private saveToStorage(): void {
    const bookmarks = Array.from(this.bookmarksSubject.value);
    localStorage.setItem(this.storageKey, JSON.stringify(bookmarks));
  }

  toggleBookmark(jobOfferId: number): void {
    const current = this.bookmarksSubject.value;
    
    if (current.has(jobOfferId)) {
      current.delete(jobOfferId);
    } else {
      current.add(jobOfferId);
    }
    
    this.bookmarksSubject.next(new Set(current));
    this.saveToStorage();
  }

  isBookmarked(jobOfferId: number): boolean {
    return this.bookmarksSubject.value.has(jobOfferId);
  }

  getBookmarkedJobIds(): number[] {
    return Array.from(this.bookmarksSubject.value);
  }
}