import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  private authService = inject(AuthService);
  private router = inject(Router);

  nombreUsuario = '';
  contrasena = '';
  mensajeError = '';
  cargando = false;

  iniciarSesion(): void {
    this.mensajeError = '';

    if (!this.nombreUsuario.trim() || !this.contrasena) {
      this.mensajeError = 'Ingresa tu usuario y contraseña.';
      return;
    }

    this.cargando = true;

    this.authService.login({
      nombreUsuario: this.nombreUsuario,
      contrasena: this.contrasena
    }).subscribe({
      next: (respuesta) => {
        this.cargando = false;

        if (respuesta.rol === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else if (respuesta.rol === 'RECEPCIONISTA') {
          this.router.navigate(['/recepcion/clientes']);
        } else {
          this.mensajeError = 'El usuario no tiene un rol válido.';
        }
      },
      error: (error) => {
        this.cargando = false;

        if (error.status === 401 || error.status === 403) {
          this.mensajeError = 'Usuario o contraseña incorrectos.';
        } else {
          this.mensajeError = 'No se pudo conectar con el servidor.';
        }
      }
    });
  }
}