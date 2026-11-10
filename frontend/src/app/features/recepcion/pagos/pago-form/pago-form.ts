import {
  Component,
  OnInit,
  ChangeDetectorRef,
  inject
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import {
  Pago,
  PagoRequest,
  MetodoPago
} from '../models/pago.model';

import { PagoService } from '../services/pago.service';

@Component({
  selector: 'app-pago-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pago-form.html',
  styleUrl: './pago-form.css'
})
export class PagoForm implements OnInit {

  private pagoService = inject(PagoService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  pagosPendientes: Pago[] = [];

  pagoSeleccionado: Pago | null = null;
  comprobante: Pago | null = null;

  metodo: MetodoPago = 'EFECTIVO';
  otroMetodo = '';

  cargando = false;
  guardando = false;
  mensaje = '';

  ngOnInit(): void {
    this.cargarPagosPendientes();
  }

  cargarPagosPendientes(): void {
    this.cargando = true;
    this.mensaje = '';

    this.pagoService.listar().subscribe({
      next: datos => {
        this.pagosPendientes = datos.filter(
          pago => pago.estado === 'PENDIENTE'
        );

        this.cargando = false;
        this.cdr.markForCheck();
      },

      error: () => {
        this.mensaje = 'No se pudieron cargar los pagos pendientes.';
        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  seleccionarPago(): void {
    this.comprobante = null;

    if (!this.pagoSeleccionado) {
      return;
    }

    this.pagoSeleccionado =
      this.pagosPendientes.find(
        pago => pago.idPago === Number(this.pagoSeleccionado!.idPago)
      ) || null;
  }

  confirmar(): void {
    this.mensaje = '';

    if (!this.pagoSeleccionado) {
      this.mensaje = 'Selecciona un pago pendiente.';
      return;
    }

    if (this.metodo === 'OTRO' && !this.otroMetodo.trim()) {
      this.mensaje = 'Especifica el medio de pago.';
      return;
    }

    if (this.metodo === 'OTRO' && this.otroMetodo.trim().length > 50) {
      this.mensaje = 'El medio de pago no puede superar 50 caracteres.';
      return;
    }

    const datos: PagoRequest = {
      metodo: this.metodo
    };

    if (this.metodo === 'OTRO') {
      datos.otroMetodo = this.otroMetodo.trim();
    }

    this.guardando = true;
    this.cdr.markForCheck();

    this.pagoService.confirmar(
      this.pagoSeleccionado.idPago,
      datos
    ).subscribe({
      next: pago => {
        this.comprobante = pago;

        this.pagosPendientes = this.pagosPendientes.filter(
          p => p.idPago !== pago.idPago
        );

        this.pagoSeleccionado = null;
        this.otroMetodo = '';
        this.guardando = false;

        this.cdr.markForCheck();
      },

      error: error => {
        this.guardando = false;

        this.mensaje =
          error.error?.detail ||
          error.error?.mensaje ||
          'No se pudo confirmar el pago.';

        this.cdr.markForCheck();
      }
    });
  }

  imprimir(): void {
    window.print();
  }

  volver(): void {
    this.router.navigate(['/recepcion/pagos']);
  }
}