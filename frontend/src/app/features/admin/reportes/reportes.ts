import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Reserva } from '../../recepcion/reservas/models/reserva.model';
import { ReservaService } from '../../recepcion/reservas/services/reserva.service';

import { Pago } from '../../recepcion/pagos/models/pago.model';
import { PagoService } from '../../recepcion/pagos/services/pago.service';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css'
})
export class Reportes implements OnInit {

  private reservaService = inject(ReservaService);
  private pagoService = inject(PagoService);

  reservas: Reserva[] = [];
  pagos: Pago[] = [];

  fechaInicio = '';
  fechaFin = '';

  cargando = false;
  mensaje = '';

  totalReservas = 0;
  reservasConfirmadas = 0;
  reservasCanceladas = 0;
  totalPagos = 0;
  ingresosTotales = 0;
  pagosCompletados = 0;
  pagosRechazados = 0;
  pagosReembolsados = 0;

  ngOnInit(): void {
    this.cargarReportes();
  }

  cargarReportes(): void {
    this.cargando = true;
    this.mensaje = '';

    let reservasCargadas = false;
    let pagosCargados = false;
    let huboError = false;

    const finalizarCarga = (): void => {
      if (reservasCargadas && pagosCargados) {
        this.cargando = false;
        this.calcularEstadisticas();

        if (huboError) {
          this.mensaje = 'Algunos datos no pudieron cargarse.';
        }
      }
    };

    this.reservaService.listar().subscribe({
      next: datos => {
        this.reservas = datos;
        reservasCargadas = true;
        finalizarCarga();
      },
      error: () => {
        huboError = true;
        reservasCargadas = true;
        finalizarCarga();
      }
    });

    this.pagoService.listar().subscribe({
      next: datos => {
        this.pagos = datos;
        pagosCargados = true;
        finalizarCarga();
      },
      error: () => {
        huboError = true;
        pagosCargados = true;
        finalizarCarga();
      }
    });
  }

  obtenerReservasFiltradas(): Reserva[] {
    return this.reservas.filter(reserva => {
      const fecha = new Date(reserva.fechaInicio);

      if (this.fechaInicio && fecha < new Date(`${this.fechaInicio}T00:00:00`)) {
        return false;
      }

      if (this.fechaFin && fecha > new Date(`${this.fechaFin}T23:59:59`)) {
        return false;
      }

      return true;
    });
  }

  obtenerPagosFiltrados(): Pago[] {
    return this.pagos.filter(pago => {
    if (pago.fechaPago === null) {
      return;
    }

    const fecha = new Date(pago.fechaPago);


      if (this.fechaInicio && fecha < new Date(`${this.fechaInicio}T00:00:00`)) {
        return false;
      }

      if (this.fechaFin && fecha > new Date(`${this.fechaFin}T23:59:59`)) {
        return false;
      }

      return true;
    });
  }

  calcularEstadisticas(): void {
    const reservas = this.obtenerReservasFiltradas();
    const pagos = this.obtenerPagosFiltrados();

    this.totalReservas = reservas.length;

    this.reservasConfirmadas = reservas.filter(
      r => r.estado === 'CONFIRMADA'
    ).length;

    this.reservasCanceladas = reservas.filter(
      r => r.estado === 'CANCELADA'
    ).length;

    this.totalPagos = pagos.length;

    this.pagosCompletados = pagos.filter(
      p => p.estado === 'COMPLETADO'
    ).length;

    this.pagosRechazados = pagos.filter(
      p => p.estado === 'RECHAZADO'
    ).length;

    this.pagosReembolsados = pagos.filter(
      p => p.estado === 'REEMBOLSADO'
    ).length;

    this.ingresosTotales = pagos
      .filter(p => p.estado === 'COMPLETADO')
      .reduce((total, pago) => total + Number(pago.monto), 0);
  }

  filtrar(): void {
    this.calcularEstadisticas();
  }

  limpiarFiltros(): void {
    this.fechaInicio = '';
    this.fechaFin = '';
    this.calcularEstadisticas();
  }
}