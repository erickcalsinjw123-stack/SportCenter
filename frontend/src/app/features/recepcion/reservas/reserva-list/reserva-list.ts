import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Reserva } from '../models/reserva.model';
import { ReservaService } from '../services/reserva.service';

@Component({
  selector: 'app-reserva-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './reserva-list.html',
  styleUrl: './reserva-list.css'
})
export class ReservaList implements OnInit {

  private service = inject(ReservaService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  reservas: Reserva[] = [];
  reservasFiltradas: Reserva[] = [];

  busqueda = '';
  filtroEstado = '';
  cargando = false;
  mensaje = '';

  mostrarModalCancelar = false;
  reservaSeleccionada: Reserva | null = null;
  cancelando = false;

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.mensaje = '';

    this.service.listar().subscribe({
      next: datos => {
        this.reservas = datos;
        this.filtrar();
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.mensaje = 'No se pudieron cargar las reservas.';
        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  filtrar(): void {
    const texto = this.busqueda.trim().toLowerCase();

    this.reservasFiltradas = this.reservas.filter(reserva => {
      const coincideTexto =
        String(reserva.idReserva).includes(texto) ||
        reserva.nombreCliente.toLowerCase().includes(texto) ||
        reserva.nombreCancha.toLowerCase().includes(texto) ||
        reserva.deporte.toLowerCase().includes(texto);

      const coincideEstado =
        !this.filtroEstado || reserva.estado === this.filtroEstado;

      return coincideTexto && coincideEstado;
    });
  }

  agregar(): void {
    this.router.navigate(['/recepcion/reservas/nueva']);
  }

  modificar(reserva: Reserva): void {
    if (reserva.estado === 'CANCELADA') return;

    this.router.navigate(['/recepcion/reservas/editar', reserva.idReserva]);
  }

  abrirModalCancelar(reserva: Reserva): void {
    if (reserva.estado === 'CANCELADA') return;

    this.reservaSeleccionada = reserva;
    this.mostrarModalCancelar = true;
  }

  cerrarModalCancelar(): void {
    if (this.cancelando) return;

    this.mostrarModalCancelar = false;
    this.reservaSeleccionada = null;
  }

  confirmarCancelacion(): void {
    if (!this.reservaSeleccionada) return;

    this.cancelando = true;

    this.service.cancelar(this.reservaSeleccionada.idReserva).subscribe({
      next: () => {
        this.mostrarModalCancelar = false;
        this.reservaSeleccionada = null;
        this.cancelando = false;
        this.mensaje = 'Reserva cancelada correctamente.';
        this.cargar();
      },
      error: error => {
        this.cancelando = false;
        this.mensaje =
          error.error?.detail ||
          error.error?.mensaje ||
          'No se pudo cancelar la reserva.';
        this.mostrarModalCancelar = false;
        this.reservaSeleccionada = null;
      }
    });
  }
}