
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type Rol = 'ADMIN' | 'RECEPCIONISTA';

export interface LoginRequest {
  nombreUsuario: string;
  contrasena: string;
}

export interface LoginResponse {
  mensaje: string;
  usuario: string;
  rol: Rol;
}

export interface SessionResponse {
  usuario: string;
  rol: Rol;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/auth';

  login(datos: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.apiUrl}/login`,
      datos,
      { withCredentials: true }
    );
  }

  iniciarSesion(datos: LoginRequest): Observable<LoginResponse> {
    return this.login(datos);
  }

  obtenerSesion(): Observable<SessionResponse> {
    return this.http.get<SessionResponse>(
      `${this.apiUrl}/me`,
      { withCredentials: true }
    );
  }

  cerrarSesion(): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/logout`,
      {},
      { withCredentials: true }
    );
  }
}
