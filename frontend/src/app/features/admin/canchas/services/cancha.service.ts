import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cancha, CanchaRequest } from '../models/cancha.model';

@Injectable({
  providedIn: 'root'
})
export class CanchaService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/canchas';

  listar(): Observable<Cancha[]> {
    return this.http.get<Cancha[]>(this.apiUrl, {
      withCredentials: true
    });
  }

  buscar(id: number): Observable<Cancha> {
    return this.http.get<Cancha>(`${this.apiUrl}/${id}`, {
      withCredentials: true
    });
  }

  registrar(datos: CanchaRequest): Observable<Cancha> {
    return this.http.post<Cancha>(this.apiUrl, datos, {
      withCredentials: true
    });
  }

  actualizar(id: number, datos: CanchaRequest): Observable<Cancha> {
    return this.http.put<Cancha>(`${this.apiUrl}/${id}`, datos, {
      withCredentials: true
    });
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, {
      withCredentials: true
    });
  }
}