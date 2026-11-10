import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { Cancha } from '../models/cancha.model';
import { CanchaService } from '../services/cancha.service';

@Component({
  selector: 'app-cancha-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './cancha-list.html',
  styleUrl: './cancha-list.css'
})
export class CanchaList implements OnInit {

  private service = inject(CanchaService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  canchas: Cancha[] = [];
  canchasFiltradas: Cancha[] = [];
  cargando = false;
  mensaje = '';
  busqueda = '';

  mostrarModalEliminar = false;
  canchaSeleccionada: Cancha | null = null;
  eliminando = false;

  mostrarModalConsulta = false;
  canchaConsultada: Cancha | null = null;

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.mensaje = '';

    this.service.listar().subscribe({
      next: (datos) => {
        this.canchas = datos;
        this.filtrar();
        this.cargando = false;

        this.cdr.markForCheck();
      },
      error: () => {
        this.mensaje = 'No se pudieron cargar las canchas.';
        this.cargando = false;

        this.cdr.markForCheck();
      }
    });
  }

  filtrar(): void {
    const texto = this.busqueda.trim().toLowerCase();

    this.canchasFiltradas = this.canchas.filter(cancha =>
      cancha.nombre.toLowerCase().includes(texto) ||
      cancha.estado.toLowerCase().includes(texto) ||
      String(cancha.idCancha).includes(texto)
    );
  }

  agregar(): void {
    this.router.navigate(['/admin/canchas/nueva']);
  }

  modificar(cancha: Cancha): void {
    this.router.navigate(['/admin/canchas/editar', cancha.idCancha]);
  }

  consultar(cancha: Cancha): void {
    this.canchaConsultada = cancha;
    this.mostrarModalConsulta = true;
  }

  cerrarConsulta(): void {
    this.mostrarModalConsulta = false;
    this.canchaConsultada = null;
  }

  abrirModalEliminar(cancha: Cancha): void {
    this.canchaSeleccionada = cancha;
    this.mostrarModalEliminar = true;
  }

  cerrarModalEliminar(): void {
    if (this.eliminando) return;

    this.mostrarModalEliminar = false;
    this.canchaSeleccionada = null;
  }

  confirmarEliminacion(): void {
    if (!this.canchaSeleccionada || this.eliminando) return;

    this.eliminando = true;
    const id = this.canchaSeleccionada.idCancha;

    this.service.eliminar(id).subscribe({
      next: () => {
        this.mensaje = 'Cancha eliminada correctamente.';
        this.mostrarModalEliminar = false;
        this.canchaSeleccionada = null;
        this.eliminando = false;
        this.cargar();
      },
      error: (error) => {
        this.mensaje =
          error.error?.detail ||
          error.error?.mensaje ||
          'No se pudo eliminar la cancha. Puede tener reservas asociadas.';

        this.eliminando = false;
      }
    });
  }
}