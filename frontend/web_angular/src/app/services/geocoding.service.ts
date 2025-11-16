// geocoding.service.ts - VERSION AMÉLIORÉE
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GeocodingService {
  private apiKey = 'b39fbadb540d40dfb9cdb4907ad2b8d8';
  private apiUrl = 'https://api.opencagedata.com/geocode/v1/json';

  constructor(private http: HttpClient) {}

  geocodeCity(cityName: string): Observable<any> {
    if (cityName.length < 2) return of(null);
    const url = `${this.apiUrl}?q=${encodeURIComponent(cityName + ', Tunisia')}&key=${this.apiKey}&limit=1`;
    return this.http.get(url).pipe(
      catchError(error => {
        console.error('OpenCage API error:', error);
        return of(null);
      })
    );
  }

  getCityFromCoords(lat: number, lng: number): Observable<any> {
    const url = `${this.apiUrl}?q=${lat}+${lng}&key=${this.apiKey}&limit=3&no_annotations=1`;
    console.log('🔄 Reverse geocoding for:', lat, lng);
    
    return this.http.get(url).pipe(
      catchError(error => {
        console.error('Reverse geocoding error:', error);
        return of(null);
      })
    );
  }

  // REAL GEOLOCATION WITH BETTER ACCURACY
  getCurrentPosition(): Promise<{lat: number, lng: number, accuracy?: number}> {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject('Geolocation not supported by your browser');
        return;
      }

      console.log('🌍 Requesting precise GPS position...');

      navigator.geolocation.getCurrentPosition(
        (position) => {
          const coords = {
            lat: position.coords.latitude,
            lng: position.coords.longitude,
            accuracy: position.coords.accuracy
          };
          console.log('✅ Real position detected:', coords);
          console.log('📏 Accuracy:', coords.accuracy + ' meters');
          resolve(coords);
        },
        (error) => {
          let errorMessage = 'Cannot get your position';
          switch (error.code) {
            case error.PERMISSION_DENIED:
              errorMessage = 'Location permission denied';
              break;
            case error.POSITION_UNAVAILABLE:
              errorMessage = 'Position unavailable - check your GPS';
              break;
            case error.TIMEOUT:
              errorMessage = 'Location request timeout';
              break;
          }
          reject(errorMessage);
        },
        {
          enableHighAccuracy: true,  // Force GPS usage
          timeout: 20000,           // 20 seconds
          maximumAge: 0             // No cached position
        }
      );
    });
  }
}