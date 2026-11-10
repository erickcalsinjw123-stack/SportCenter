import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reserva, ReservaRequest } from '../models/reserva.model';

@Injectable({
  providedIn: 'root'
})
export class ReservaService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/reservas';

  listar(): Observable<Reserva[]> {
    return this.http.get<Reserva[]>(this.apiUrl, {
      withCredentials: true
    });
  }

  buscar(id: number): Observable<Reserva> {
    return this.http.get<Reserva>(`${this.apiUrl}/${id}`, {
      withCredentials: true
    });
  }

  registrar(datos: ReservaRequest): Observable<Reserva> {
    return this.http.post<Reserva>(this.apiUrl, datos, {
      withCredentials: true
    });
  }

  actualizar(id: number, datos: ReservaRequest): Observable<Reserva> {
    return this.http.put<Reserva>(`${this.apiUrl}/${id}`, datos, {
      withCredentials: true
    });
  }

  cancelar(id: number): Observable<void> {
    return this.http.patch<void>(
      `${this.apiUrl}/${id}/cancelar`,
      {},
      { withCredentials: true }
    );
  }
}