import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ClienteService } from '../services/cliente.service';
import { ClienteRequest } from '../models/cliente.model';


@Component({
  selector: 'app-cliente-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cliente-form.html',
  styleUrl: './cliente-form.css'
})
export class ClienteForm implements OnInit {

  private service = inject(ClienteService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef);

  id: number | null = null;
  modoEdicion = false;
  guardando = false;
  mensaje = '';

  cliente: ClienteRequest = {
    nombre: '',
    apellido: '',
    telefono: '',
    email: ''
  };

  ngOnInit(): void {
    const parametro = this.route.snapshot.paramMap.get('id');

    if (parametro) {
      this.id = Number(parametro);
      this.modoEdicion = true;
      this.cargarCliente();
    }
  }

  cargarCliente(): void {
    if (!this.id) return;

    this.service.buscar(this.id).subscribe({
      next: datos => {
        this.cliente = {
          nombre: datos.nombre,
          apellido: datos.apellido,
          telefono: datos.telefono,
          email: datos.email
        };

        this.cdr.markForCheck();
      },
      error: () => {
        this.mensaje = 'No se pudo cargar la información del cliente.';
        this.cdr.markForCheck();
      }
    });
  }

  guardar(): void {
    if (this.guardando) return;

    const nombre = this.cliente.nombre.trim();
    const apellido = this.cliente.apellido.trim();
    const telefono = this.cliente.telefono.trim();
    const email = this.cliente.email.trim();

    if (!nombre || !apellido || !/^[0-9]{9}$/.test(telefono) ||
      !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      this.mensaje = 'Verifica los campos antes de continuar.';
      return;
    }

    this.guardando = true;
    this.mensaje = '';

    const datos: ClienteRequest = {
      nombre,
      apellido,
      telefono,
      email
    };

    const operacion = this.modoEdicion && this.id
      ? this.service.actualizar(this.id, datos)
      : this.service.registrar(datos);

    operacion.subscribe({
      next: () => {
        this.router.navigate(['/recepcion/clientes']);
      },
      error: error => {
        this.mensaje =
          error.error?.detail ||
          error.error?.mensaje ||
          'No se pudo guardar el cliente. Verifica sus datos.';

        this.guardando = false;
        this.cdr.markForCheck();
      }
      
    });
  }

  cancelar(): void {
    this.router.navigate(['/recepcion/clientes']);
  }
}