import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Pago, PagoRequest } from '../models/pago.model';

@Injectable({
  providedIn: 'root'
})
export class PagoService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/pagos';

  listar(): Observable<Pago[]> {
    return this.http.get<Pago[]>(this.apiUrl, {
      withCredentials: true
    });
  }

  buscar(id: number): Observable<Pago> {
    return this.http.get<Pago>(`${this.apiUrl}/${id}`, {
      withCredentials: true
    });
  }

  confirmar(id: number, datos: PagoRequest): Observable<Pago> {
    return this.http.patch<Pago>(
      `${this.apiUrl}/${id}/confirmar`,
      datos,
      { withCredentials: true }
    );
  }
}