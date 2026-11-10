import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente, ClienteRequest } from '../models/cliente.model';

@Injectable({ providedIn: 'root' })
export class ClienteService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/clientes';

  listar(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.apiUrl, { withCredentials: true });
  }

  buscar(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  registrar(datos: ClienteRequest): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, datos, { withCredentials: true });
  }

  actualizar(id: number, datos: ClienteRequest): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/${id}`, datos, { withCredentials: true });
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }
}