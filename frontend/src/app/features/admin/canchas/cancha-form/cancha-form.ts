import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { CanchaService } from '../services/cancha.service';
import { CanchaRequest } from '../models/cancha.model';

@Component({
  selector: 'app-cancha-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './cancha-form.html',
  styleUrl: './cancha-form.css'
})
export class CanchaForm implements OnInit {

  private service = inject(CanchaService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  id: number | null = null;
  cargando = false;
  mensaje = '';

  datos: CanchaRequest = {
    nombre: '',
    precioHora: 0,
    estado: 'DISPONIBLE'
  };

  get editando(): boolean {
    return this.id !== null;
  }

  ngOnInit(): void {
    const valor = this.route.snapshot.paramMap.get('id');

    if (valor !== null) {
      this.id = Number(valor);

      if (Number.isNaN(this.id)) {
        this.router.navigate(['/admin/canchas']);
        return;
      }

      this.cargarCancha();
    }
  }

  cargarCancha(): void {
    if (this.id === null) return;

    this.cargando = true;

    this.service.buscar(this.id).subscribe({
      next: (cancha) => {
        this.datos = {
          nombre: cancha.nombre,
          precioHora: cancha.precioHora,
          estado: cancha.estado
        };

        this.cargando = false;
      },
      error: () => {
        this.mensaje = 'No se pudo cargar la información de la cancha.';
        this.cargando = false;
      }
    });
  }

  guardar(): void {
    this.mensaje = '';

    const nombre = this.datos.nombre.trim();

    if (!nombre) {
      this.mensaje = 'El nombre de la cancha es obligatorio.';
      return;
    }

    if (nombre.length > 30) {
      this.mensaje = 'El nombre no puede superar los 30 caracteres.';
      return;
    }

    if (!this.datos.precioHora || this.datos.precioHora <= 0) {
      this.mensaje = 'Ingresa un precio por hora mayor que cero.';
      return;
    }

    this.cargando = true;

    const peticion = this.editando && this.id !== null
      ? this.service.actualizar(this.id, {
          ...this.datos,
          nombre
        })
      : this.service.registrar({
          ...this.datos,
          nombre
        });

    peticion.subscribe({
      next: () => {
        this.router.navigate(['/admin/canchas']);
      },
      error: (error: HttpErrorResponse) => {
        this.mensaje =
          error.error?.detail ||
          error.error?.mensaje ||
          'No se pudo guardar la cancha.';

        this.cargando = false;
      }
    });
  }
}