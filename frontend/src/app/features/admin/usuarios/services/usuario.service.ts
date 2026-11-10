import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario, UsuarioRequest } from '../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/usuarios';

  listar(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.apiUrl, {
      withCredentials: true
    });
  }

  buscar(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/${id}`, {
      withCredentials: true
    });
  }

  registrar(datos: UsuarioRequest): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrl, datos, {
      withCredentials: true
    });
  }

  actualizar(id: number, datos: UsuarioRequest): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}`, datos, {
      withCredentials: true
    });
  }

  cambiarEstado(id: number, estado: 'ACTIVO' | 'INACTIVO'): Observable<Usuario> {
    return this.http.patch<Usuario>(
      `${this.apiUrl}/${id}/estado`,
      { estado },
      { withCredentials: true }
    );
  }

  eliminar(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      withCredentials: true
    });
  }
}