
import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Reserva, ReservaRequest } from '../models/reserva.model';
import { ReservaService } from '../services/reserva.service';

import { ClienteService } from '../../clientes/services/cliente.service';
import { Cliente } from '../../clientes/models/cliente.model';

import { CanchaService } from '../../../admin/canchas/services/cancha.service';
import { Cancha } from '../../../admin/canchas/models/cancha.model';

@Component({
  selector: 'app-reserva-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reserva-form.html',
  styleUrl: './reserva-form.css'
})
export class ReservaForm implements OnInit {

  private reservaService = inject(ReservaService);
  private clienteService = inject(ClienteService);
  private canchaService = inject(CanchaService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  clientes: Cliente[] = [];
  canchas: Cancha[] = [];

  idReserva: number | null = null;
  esEdicion = false;
  cargando = false;
  guardando = false;
  mensaje = '';

  fechaReserva = '';
  horaInicio = '';
  horaFin = '';

  horasInicio: string[] = [];
  horasFin: string[] = [];

  reserva: ReservaRequest = {
    idCliente: 0,
    idCancha: 0,
    deporte: '',
    fechaInicio: '',
    fechaFin: ''
  };

  precioTotal = 0;

  ngOnInit(): void {
    this.generarHorarios();
    this.cargarClientes();
    this.cargarCanchas();

    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.idReserva = Number(id);
      this.esEdicion = true;
      this.cargarReserva(this.idReserva);
    }
  }

  generarHorarios(): void {
    this.horasInicio = [];
    this.horasFin = [];

    // Inicio: 08:00 hasta 23:45.
    for (let minutos = 8 * 60; minutos < 24 * 60; minutos += 15) {
      this.horasInicio.push(this.formatearHora(minutos));
    }

    // Fin: 08:15 hasta 00:00 del día siguiente.
    for (let minutos = 8 * 60 + 15; minutos <= 24 * 60; minutos += 15) {
      this.horasFin.push(
        minutos === 24 * 60 ? '00:00' : this.formatearHora(minutos)
      );
    }
  }

  private formatearHora(minutos: number): string {
    const horas = Math.floor(minutos / 60);
    const mins = minutos % 60;

    return `${String(horas).padStart(2, '0')}:${String(mins).padStart(2, '0')}`;
  }

  actualizarFechas(): void {
    this.mensaje = '';

    if (!this.fechaReserva || !this.horaInicio) {
      this.reserva.fechaInicio = '';
      this.reserva.fechaFin = '';
      this.precioTotal = 0;
      return;
    }

    this.reserva.fechaInicio = `${this.fechaReserva}T${this.horaInicio}:00`;

    if (this.horaFin) {
      let fechaFin = this.fechaReserva;

      // 00:00 corresponde a la medianoche del día siguiente.
      if (this.horaFin === '00:00') {
        fechaFin = this.sumarDia(this.fechaReserva);
      }

      this.reserva.fechaFin = `${fechaFin}T${this.horaFin}:00`;
    } else {
      this.reserva.fechaFin = '';
    }

    this.calcularPrecio();
  }

  private sumarDia(fecha: string): string {
    const [anio, mes, dia] = fecha.split('-').map(Number);
    const nuevaFecha = new Date(anio, mes - 1, dia + 1);

    const nuevoAnio = nuevaFecha.getFullYear();
    const nuevoMes = String(nuevaFecha.getMonth() + 1).padStart(2, '0');
    const nuevoDia = String(nuevaFecha.getDate()).padStart(2, '0');

    return `${nuevoAnio}-${nuevoMes}-${nuevoDia}`;
  }

  cargarClientes(): void {
    this.clienteService.listar().subscribe({
      next: datos => {
        this.clientes = datos;
        this.cdr.markForCheck();
      },
      error: () => {
        this.mensaje = 'No se pudieron cargar los clientes.';
        this.cdr.markForCheck();
      }
    });
  }

  cargarCanchas(): void {
    this.canchaService.listar().subscribe({
      next: datos => {
        this.canchas = datos.filter(
          c => String(c.estado).trim().toUpperCase() === 'DISPONIBLE'
        );

        this.cdr.markForCheck();
      },
      error: () => {
        this.mensaje = 'No se pudieron cargar las canchas.';
        this.cdr.markForCheck();
      }
    });
  }

  cargarReserva(id: number): void {
    this.cargando = true;

    this.reservaService.buscar(id).subscribe({
      next: datos => {
        this.reserva = {
          idCliente: datos.idCliente,
          idCancha: datos.idCancha,
          deporte: datos.deporte,
          fechaInicio: datos.fechaInicio,
          fechaFin: datos.fechaFin
        };

        const inicio = new Date(datos.fechaInicio);
        const fin = new Date(datos.fechaFin);

        this.fechaReserva = this.fechaLocal(inicio);
        this.horaInicio = this.formatearHora(
          inicio.getHours() * 60 + inicio.getMinutes()
        );

        this.horaFin = this.fechaLocal(fin) !== this.fechaReserva
          ? '00:00'
          : this.formatearHora(fin.getHours() * 60 + fin.getMinutes());

        this.calcularPrecio();
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.mensaje = 'No se pudo cargar la reserva.';
        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  private fechaLocal(fecha: Date): string {
    const anio = fecha.getFullYear();
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');

    return `${anio}-${mes}-${dia}`;
  }

  calcularPrecio(): void {
    const cancha = this.canchas.find(
      c => c.idCancha === Number(this.reserva.idCancha)
    );

    if (!cancha || !this.reserva.fechaInicio || !this.reserva.fechaFin) {
      this.precioTotal = 0;
      return;
    }

    const inicio = new Date(this.reserva.fechaInicio).getTime();
    const fin = new Date(this.reserva.fechaFin).getTime();

    const horas = (fin - inicio) / (1000 * 60 * 60);

    this.precioTotal = horas > 0
      ? horas * cancha.precioHora
      : 0;
  }

  guardar(): void {
    this.mensaje = '';

    if (
      !this.reserva.idCliente ||
      !this.reserva.idCancha ||
      !this.reserva.deporte ||
      !this.fechaReserva ||
      !this.horaInicio ||
      !this.horaFin
    ) {
      this.mensaje = 'Completa todos los campos.';
      return;
    }

    this.actualizarFechas();

    const inicio = new Date(this.reserva.fechaInicio);
    const fin = new Date(this.reserva.fechaFin);

    if (fin <= inicio) {
      this.mensaje = 'La hora de fin debe ser posterior a la hora de inicio.';
      return;
    }

    if (inicio.getMinutes() % 15 !== 0 || fin.getMinutes() % 15 !== 0) {
      this.mensaje = 'Los horarios deben respetar intervalos de 15 minutos.';
      return;
    }

    if (inicio.getHours() < 8 || inicio.getHours() >= 24) {
      this.mensaje = 'Las reservas deben iniciar desde las 08:00.';
      return;
    }

    const limite = new Date(inicio);
    limite.setDate(limite.getDate() + 1);
    limite.setHours(0, 0, 0, 0);

    if (fin > limite) {
      this.mensaje = 'La reserva debe finalizar como máximo a las 00:00.';
      return;
    }

    this.calcularPrecio();

    if (this.precioTotal <= 0) {
      this.mensaje = 'Verifica el horario y la cancha seleccionada.';
      return;
    }

    this.guardando = true;

    const operacion = this.esEdicion && this.idReserva
      ? this.reservaService.actualizar(this.idReserva, this.reserva)
      : this.reservaService.registrar(this.reserva);

    operacion.subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/recepcion/reservas']);
      },
      error: error => {
        this.guardando = false;
        this.mensaje =
          error.error?.detail ||
          error.error?.mensaje ||
          'No se pudo guardar la reserva. Verifica que el horario esté disponible.';

        this.cdr.markForCheck();
      }
    });
  }

  volver(): void {
    this.router.navigate(['/recepcion/reservas']);
  }
}