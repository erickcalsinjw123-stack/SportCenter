import {
  Component,
  OnInit,
  ChangeDetectorRef,
  inject
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { Pago } from '../models/pago.model';
import { PagoService } from '../services/pago.service';

@Component({
  selector: 'app-pago-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pago-list.html',
  styleUrl: './pago-list.css'
})
export class PagoList implements OnInit {

  private service = inject(PagoService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  pagos: Pago[] = [];
  pagosFiltrados: Pago[] = [];

  busqueda = '';
  filtroEstado = '';
  cargando = false;
  mensaje = '';

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.mensaje = '';
    this.cdr.markForCheck();

    this.service.listar().subscribe({
      next: datos => {
        this.pagos = datos;
        this.filtrar();

        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.mensaje = 'No se pudieron cargar los pagos.';
        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  filtrar(): void {
    const texto = this.busqueda.trim().toLowerCase();

    this.pagosFiltrados = this.pagos.filter(pago => {

      const coincideTexto =
        String(pago.idPago).includes(texto) ||
        String(pago.idReserva).includes(texto) ||
        (pago.nombreCliente || '').toLowerCase().includes(texto) ||
        (pago.nombreCancha || '').toLowerCase().includes(texto) ||
        (pago.metodo || '').toLowerCase().includes(texto);

      const coincideEstado =
        !this.filtroEstado || pago.estado === this.filtroEstado;

      return coincideTexto && coincideEstado;
    });
  }

  registrar(): void {
    this.router.navigate(['/recepcion/pagos/nuevo']);
  }

  procesarPago(): void {
    this.registrar();
  }
}