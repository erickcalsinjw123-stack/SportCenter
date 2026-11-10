import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { Usuario } from '../models/usuario.model';
import { UsuarioService } from '../services/usuario.service';

@Component({
  selector: 'app-usuario-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuario-list.html',
  styleUrl: './usuario-list.css'
})
export class UsuarioList implements OnInit {

  private service = inject(UsuarioService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  usuarios: Usuario[] = [];
  usuariosFiltrados: Usuario[] = [];

  busqueda = '';
  filtroRol = '';
  filtroEstado = '';

  cargando = false;
  mensaje = '';

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.mensaje = '';

    this.service.listar().subscribe({
      next: datos => {
        this.usuarios = datos;
        this.filtrar();
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: error => {
        console.error('Error al cargar usuarios:', error);
        this.mensaje = 'No se pudieron cargar los usuarios.';
        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  filtrar(): void {
    const texto = this.busqueda.trim().toLowerCase();

    this.usuariosFiltrados = this.usuarios.filter(usuario => {
      const coincideTexto =
        usuario.nombreUsuario.toLowerCase().includes(texto);

      const coincideRol =
        !this.filtroRol || usuario.rol === this.filtroRol;

      const coincideEstado =
        !this.filtroEstado || usuario.estado === this.filtroEstado;

      return coincideTexto && coincideRol && coincideEstado;
    });
  }

  nuevo(): void {
    this.router.navigate(['/admin/usuarios/nuevo']);
  }

  editar(usuario: Usuario): void {
    this.router.navigate(['/admin/usuarios/editar', usuario.idUsuario]);
  }

  eliminar(usuario: Usuario): void {

    const confirmar = confirm(
      `¿Estás seguro de eliminar definitivamente al usuario "${usuario.nombreUsuario}"?`
    );

    if (!confirmar) return;

    this.mensaje = '';

    this.service.eliminar(usuario.idUsuario).subscribe({

      next: () => {
        this.mensaje = 'Usuario eliminado correctamente.';
        this.cargar();
      },

      error: error => {

        if (error.status === 409) {
          this.mensaje =
            error.error?.detail ||
            'No se puede eliminar porque el usuario tiene reservas registradas.';

        } else if (error.status === 400) {
          this.mensaje =
            error.error?.detail ||
            'No se puede eliminar este usuario por las restricciones del sistema.';

        } else if (error.status === 404) {
          this.mensaje = 'El usuario ya no existe.';

        } else {
          this.mensaje = 'No se pudo eliminar el usuario. Inténtalo nuevamente.';
        }

        this.cdr.markForCheck();
      }
    });
  }
}