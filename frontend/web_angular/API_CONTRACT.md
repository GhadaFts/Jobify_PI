# 📋 Analytics Dashboard - Backend API Contract

This document defines the expected API endpoints and response formats for the Analytics Dashboard backend.

---

## Base URL
```
http://your-backend:3000/api/admin/analytics
```

---

## 1. GET /kpi

**Description:** Fetch key performance indicators  
**Method:** GET  
**Authentication:** Required (JWT)  
**Rate Limit:** 10 calls/min

### Response
```json
{
  "data": {
    "totalJobs": 324,
    "jobsLast7Days": 48,
    "totalUsers": 5021,
    "newUsersLast7Days": 132,
    "totalApplications": 12980,
    "avgApplicationsPerJob": 40.06
  },
  "timestamp": "2025-11-13T10:30:00Z"
}
```

### Query Parameters
- `startDate` (optional): ISO date string
- `endDate` (optional): ISO date string

---

## 2. GET /jobs-posted

**Description:** Fetch jobs posted over time  
**Method:** GET  
**Authentication:** Required (JWT)  
**Response Time:** < 500ms

### Response
```json
{
  "data": [
    {
      "date": "2025-10-01",
      "count": 12
    },
    {
      "date": "2025-10-02",
      "count": 18
    }
  ],
  "timestamp": "2025-11-13T10:30:00Z",
  "filters": {
    "startDate": "2025-10-01",
    "endDate": "2025-10-31",
    "granularity": "day"
  }
}
```

### Query Parameters
- `startDate`: ISO date string (required)
- `endDate`: ISO date string (required)
- `granularity`: "day" | "week" | "month" (default: "day")

---

## 3. GET /active-vs-expired

**Description:** Fetch active vs expired jobs comparison  
**Method:** GET  
**Authentication:** Required (JWT)

### Response
```json
{
  "data": [
    {
      "date": "2025-10-01",
      "active": 230,
      "expired": 12
    },
    {
      "date": "2025-10-02",
      "active": 240,
      "expired": 14
    }
  ],
  "timestamp": "2025-11-13T10:30:00Z"
}
```

### Query Parameters
- `startDate`: ISO date string (required)
- `endDate`: ISO date string (required)
- `granularity`: "day" | "week" | "month"

---

## 4. GET /users-growth

**Description:** Fetch users growth and daily active users  
**Method:** GET  
**Authentication:** Required (JWT)

### Response
```json
{
  "data": [
    {
      "date": "2025-10-01",
      "newUsers": 35,
      "dau": 420
    },
    {
      "date": "2025-10-02",
      "newUsers": 40,
      "dau": 450
    }
  ],
  "timestamp": "2025-11-13T10:30:00Z"
}
```

### Query Parameters
- `startDate`: ISO date string (required)
- `endDate`: ISO date string (required)
- `granularity`: "day" | "week" | "month"

---

## 5. GET /funnel

**Description:** Fetch application funnel data  
**Method:** GET  
**Authentication:** Required (JWT)

### Response
```json
{
  "data": {
    "views": 10000,
    "applyClicks": 1200,
    "applications": 800,
    "interviews": 120,
    "hires": 28
  },
  "timestamp": "2025-11-13T10:30:00Z"
}
```

### Query Parameters
- `startDate` (optional): ISO date string
- `endDate` (optional): ISO date string

---

## 6. GET /apps-per-job

**Description:** Fetch application count per job  
**Method:** GET  
**Authentication:** Required (JWT)

### Response
```json
{
  "data": [
    {
      "jobId": "1001",
      "jobTitle": "Senior Frontend Developer",
      "count": 45
    },
    {
      "jobId": "1002",
      "jobTitle": "Full Stack Engineer",
      "count": 38
    }
  ],
  "timestamp": "2025-11-13T10:30:00Z"
}
```

### Query Parameters
- `limit` (optional): number (default: 10, max: 100)
- `sortBy` (optional): "count" | "date" | "title"

---

## 7. GET /top-companies

**Description:** Fetch top companies by activity  
**Method:** GET  
**Authentication:** Required (JWT)

### Response
```json
{
  "data": [
    {
      "companyName": "Tech Corp",
      "jobCount": 48,
      "applicationCount": 2050
    },
    {
      "companyName": "Microsoft",
      "jobCount": 32,
      "applicationCount": 1700
    }
  ],
  "timestamp": "2025-11-13T10:30:00Z"
}
```

### Query Parameters
- `limit` (optional): number (default: 8, max: 50)
- `sortBy` (optional): "jobCount" | "applicationCount"

---

## 8. GET /top-categories

**Description:** Fetch top job categories  
**Method:** GET  
**Authentication:** Required (JWT)

### Response
```json
{
  "data": [
    {
      "category": "Frontend Development",
      "jobs": 120,
      "applications": 2800
    },
    {
      "category": "Backend Development",
      "jobs": 105,
      "applications": 2200
    }
  ],
  "timestamp": "2025-11-13T10:30:00Z"
}
```

### Query Parameters
- `limit` (optional): number (default: 8, max: 50)
- `sortBy` (optional): "jobs" | "applications"

---

## 9. GET /geo-heatmap

**Description:** Fetch geographic distribution of jobs  
**Method:** GET  
**Authentication:** Required (JWT)

### Response
```json
{
  "data": [
    {
      "region": "California",
      "jobs": 120,
      "applications": 2800
    },
    {
      "region": "New York",
      "jobs": 95,
      "applications": 2100
    }
  ],
  "timestamp": "2025-11-13T10:30:00Z"
}
```

### Query Parameters
- `country` (optional): string (default: "US")

---

## 10. GET /alerts

**Description:** Fetch metric alerts and anomalies  
**Method:** GET  
**Authentication:** Required (JWT)

### Response
```json
{
  "data": [
    {
      "metric": "Total Applications",
      "change": -42,
      "alert": true
    },
    {
      "metric": "Job Posts",
      "change": 18,
      "alert": false
    }
  ],
  "timestamp": "2025-11-13T10:30:00Z"
}
```

### Query Parameters
- `severity` (optional): "high" | "medium" | "low"
- `limit` (optional): number (default: 6, max: 20)

---

## Error Responses

### 400 Bad Request
```json
{
  "success": false,
  "error": "Invalid date range",
  "code": "INVALID_DATE_RANGE",
  "timestamp": "2025-11-13T10:30:00Z"
}
```

### 401 Unauthorized
```json
{
  "success": false,
  "error": "Invalid or missing authentication token",
  "code": "UNAUTHORIZED",
  "timestamp": "2025-11-13T10:30:00Z"
}
```

### 403 Forbidden
```json
{
  "success": false,
  "error": "Insufficient permissions",
  "code": "FORBIDDEN",
  "timestamp": "2025-11-13T10:30:00Z"
}
```

### 429 Too Many Requests
```json
{
  "success": false,
  "error": "Rate limit exceeded",
  "code": "RATE_LIMIT",
  "timestamp": "2025-11-13T10:30:00Z",
  "retryAfter": 60
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "error": "Internal server error",
  "code": "INTERNAL_ERROR",
  "timestamp": "2025-11-13T10:30:00Z"
}
```

---

## Common Query Parameters

### All Endpoints Accept:
- `startDate`: ISO 8601 date (e.g., "2025-10-01")
- `endDate`: ISO 8601 date (e.g., "2025-10-31")
- `limit`: number (varies by endpoint)
- `offset`: number (for pagination)
- `sortBy`: string (field name)
- `sortOrder`: "asc" | "desc"

---

## Response Structure

All successful responses follow this structure:
```json
{
  "data": {
    // Endpoint-specific data
  },
  "timestamp": "ISO 8601 datetime",
  "filters": {
    // Applied filters (optional)
  },
  "pagination": {
    // Pagination info (if applicable)
    "total": 100,
    "limit": 10,
    "offset": 0
  }
}
```

---

## Authentication

All endpoints require JWT token in Authorization header:
```
Authorization: Bearer <JWT_TOKEN>
```

---

## Rate Limiting

- **Global Limit:** 100 requests/minute per user
- **Endpoint Limits:** Vary (see individual endpoint docs)
- **Response Headers:**
  ```
  X-RateLimit-Limit: 100
  X-RateLimit-Remaining: 95
  X-RateLimit-Reset: 1699861800
  ```

---

## Data Validation Rules

### Dates
- Format: ISO 8601 (YYYY-MM-DD)
- `startDate` must be ≤ `endDate`
- Maximum range: 1 year

### Numbers
- All counts must be ≥ 0
- Percentages: 0-100
- No negative values in analytics

### Strings
- Trim whitespace
- Max length: 255 characters
- UTF-8 encoding

---

## Caching Strategy

**Recommended Client-Side Caching:**
- KPI data: 5 minutes
- Charts: 10 minutes
- Alerts: 1 minute
- Filters: No caching

**Server-Side Caching (Recommended):**
- Aggregate endpoints: 1 hour
- Detailed data: 15 minutes
- Real-time data: 1 minute

---

## Example Implementation (Node.js/Express)

```javascript
// GET /api/admin/analytics/kpi
app.get('/api/admin/analytics/kpi', authenticateToken, async (req, res) => {
  try {
    const { startDate, endDate } = req.query;
    
    // Validate dates
    if (!isValidDateRange(startDate, endDate)) {
      return res.status(400).json({
        success: false,
        error: 'Invalid date range',
        code: 'INVALID_DATE_RANGE'
      });
    }
    
    // Fetch data from database
    const data = await KpiModel.findAggregates(startDate, endDate);
    
    // Return response
    res.json({
      data,
      timestamp: new Date().toISOString(),
      filters: { startDate, endDate }
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: 'Internal server error',
      code: 'INTERNAL_ERROR'
    });
  }
});
```

---

## Example Implementation (Python/Flask)

```python
@app.route('/api/admin/analytics/kpi', methods=['GET'])
@token_required
def get_kpi_data(current_user):
    try:
        start_date = request.args.get('startDate')
        end_date = request.args.get('endDate')
        
        # Validate dates
        if not validate_date_range(start_date, end_date):
            return {'error': 'Invalid date range'}, 400
        
        # Fetch from database
        data = KpiModel.query_aggregates(start_date, end_date)
        
        # Return response
        return {
            'data': data,
            'timestamp': datetime.now().isoformat(),
            'filters': {'startDate': start_date, 'endDate': end_date}
        }
    except Exception as e:
        return {'error': 'Internal server error'}, 500
```

---

## Frontend Integration Example

```typescript
// In mock-analytics.service.ts, replace mock methods:

getKpiData(): Observable<AnalyticsResponse<KpiData>> {
  const params = new HttpParams()
    .set('startDate', '2025-10-01')
    .set('endDate', '2025-10-31');
  
  return this.http.get<AnalyticsResponse<KpiData>>(
    `${this.apiUrl}/kpi`,
    { params }
  ).pipe(
    tap(response => console.log('KPI Data:', response)),
    catchError(error => {
      console.error('Error fetching KPI:', error);
      // Return fallback data or throw
      throw error;
    })
  );
}
```

---

## Testing Endpoints

### Using cURL:
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  "http://localhost:3000/api/admin/analytics/kpi?startDate=2025-10-01&endDate=2025-10-31"
```

### Using Postman:
1. Set Authorization: Bearer Token
2. Add JWT token
3. Add query parameters
4. Send GET request

### Using Angular HttpClient:
```typescript
const headers = new HttpHeaders({
  'Authorization': `Bearer ${this.authService.getToken()}`
});

this.http.get<AnalyticsResponse<KpiData>>(
  `${this.apiUrl}/kpi`,
  { headers }
).subscribe(
  response => console.log(response),
  error => console.error(error)
);
```

---

## Versioning

- Current Version: `v1`
- Expected Endpoint: `/api/v1/admin/analytics/...`
- Deprecation Policy: 6 months notice before removal

---

## Support

For API issues:
- Email: api-support@jobify.com
- Docs: https://docs.jobify.com/api
- Status: https://status.jobify.com

---

**Last Updated:** November 2025  
**API Version:** v1  
**Status:** Ready for Implementation
