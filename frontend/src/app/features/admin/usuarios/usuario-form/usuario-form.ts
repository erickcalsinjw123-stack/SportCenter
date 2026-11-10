import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Usuario, UsuarioRequest } from '../models/usuario.model';
import { UsuarioService } from '../services/usuario.service';

@Component({
  selector: 'app-usuario-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuario-form.html',
  styleUrl: './usuario-form.css'
})
export class UsuarioForm implements OnInit {

  private service = inject(UsuarioService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  idUsuario: number | null = null;
  modoEdicion = false;

  mostrarContrasena = false;
  guardando = false;
  cargando = false;
  mensaje = '';

  usuario: UsuarioRequest = {
    nombreUsuario: '',
    contrasena: '',
    rol: 'RECEPCIONISTA',
    estado: 'ACTIVO'
  };

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.idUsuario = Number(id);
      this.modoEdicion = true;
      this.cargarUsuario();
    }
  }

  cargarUsuario(): void {
  if (!this.idUsuario) return;

  this.cargando = true;

  this.service.buscar(this.idUsuario).subscribe({
    next: (datos: Usuario) => {
      this.usuario = {
        nombreUsuario: datos.nombreUsuario,
        contrasena: '',
        rol: datos.rol,
        estado: datos.estado
      };

      this.cargando = false;
      this.cdr.markForCheck();
    },
    error: (error) => {
      console.error('Error al cargar usuario:', error);

      this.mensaje = 'No se pudo cargar la información del usuario.';
      this.cargando = false;

      this.cdr.markForCheck();
    }
  });
}

  validarContrasena(contrasena: string): boolean {
    return (
      contrasena.length >= 8 &&
      /[a-z]/.test(contrasena) &&
      /[A-Z]/.test(contrasena) &&
      /[0-9]/.test(contrasena) &&
      /[^A-Za-z0-9]/.test(contrasena)
    );
  }

  guardar(): void {
    this.mensaje = '';

    if (!this.usuario.nombreUsuario.trim()) {
      this.mensaje = 'Ingresa el nombre de usuario.';
      return;
    }

    if (!this.modoEdicion && !this.usuario.contrasena) {
      this.mensaje = 'Ingresa una contraseña.';
      return;
    }

    if (
      this.usuario.contrasena &&
      !this.validarContrasena(this.usuario.contrasena)
    ) {
      this.mensaje = 'La contraseña debe tener mínimo 8 caracteres, una minúscula, una mayúscula, un número y un carácter especial.';
      return;
    }

    if (!this.usuario.rol) {
      this.mensaje = 'Selecciona un rol.';
      return;
    }

    this.guardando = true;

    const datos: UsuarioRequest = {
      nombreUsuario: this.usuario.nombreUsuario.trim(),
      rol: this.usuario.rol,
      estado: this.usuario.estado
    };

    if (this.usuario.contrasena) {
      datos.contrasena = this.usuario.contrasena;
    }

    const peticion = this.modoEdicion && this.idUsuario
      ? this.service.actualizar(this.idUsuario, datos)
      : this.service.registrar(datos);

    peticion.subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/admin/usuarios']);
      },
      error: error => {
        this.guardando = false;
        this.mensaje =
          error.error?.detail ||
          error.error?.mensaje ||
          'No se pudo guardar el usuario. Verifica los datos ingresados.';
      }
    });
  }

  volver(): void {
    this.router.navigate(['/admin/usuarios']);
  }
}