import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { ApiResponse, AuthResponse, HubUserDto } from '../models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly API = environment.apiUrl;
  private currentUserSubject = new BehaviorSubject<any>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.loadUser();
  }

  login(email: string, password: string): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.API}/auth/login`, { email, password })
      .pipe(tap(res => {
        if (res.data) {
          localStorage.setItem('accessToken', res.data.accessToken);
          localStorage.setItem('refreshToken', res.data.refreshToken);
          this.decodeAndSetUser(res.data.accessToken);
        }
      }));
  }

  refresh(): Observable<ApiResponse<AuthResponse>> {
    const refreshToken = localStorage.getItem('refreshToken');
    return this.http.post<ApiResponse<AuthResponse>>(`${this.API}/auth/refresh`, { refreshToken })
      .pipe(tap(res => {
        if (res.data) {
          localStorage.setItem('accessToken', res.data.accessToken);
          this.decodeAndSetUser(res.data.accessToken);
        }
      }));
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    return user?.roles?.includes('HubAdmin');
  }

  getCurrentUser(): any {
    return this.currentUserSubject.value;
  }

  private loadUser(): void {
    const token = this.getToken();
    if (token) {
      try {
        this.decodeAndSetUser(token);
      } catch {
        this.logout();
      }
    }
  }

  private decodeAndSetUser(token: string): void {
    const payload = JSON.parse(atob(token.split('.')[1]));
    this.currentUserSubject.next({
      id: payload.sub,
      email: payload.email,
      roles: payload.roles || [],
      programs: payload.programs || [],
    });
  }
}
