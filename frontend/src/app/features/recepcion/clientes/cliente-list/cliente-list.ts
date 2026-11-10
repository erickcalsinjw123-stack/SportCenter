
import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Cliente } from '../models/cliente.model';
import { ClienteService } from '../services/cliente.service';

@Component({
  selector: 'app-cliente-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './cliente-list.html',
  styleUrl: './cliente-list.css'
})
export class ClienteList implements OnInit {

  private service = inject(ClienteService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  clientes: Cliente[] = [];
  clientesFiltrados: Cliente[] = [];
  busqueda = '';
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
        this.clientes = datos;
        this.filtrar();
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.mensaje = 'No se pudieron cargar los clientes.';
        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  filtrar(): void {
    const texto = this.busqueda.toLowerCase().trim();

    this.clientesFiltrados = this.clientes.filter(c =>
      `${c.nombre} ${c.apellido} ${c.telefono} ${c.email}`
        .toLowerCase().includes(texto)
    );
  }

  agregar(): void {
    this.router.navigate(['/recepcion/clientes/nuevo']);
  }

  modificar(cliente: Cliente): void {
    this.router.navigate(['/recepcion/clientes/editar', cliente.idCliente]);
  }

  eliminar(cliente: Cliente): void {
    if (!confirm(`¿Deseas eliminar al cliente ${cliente.nombre} ${cliente.apellido}?`)) {
      return;
    }

    this.mensaje = '';

    this.service.eliminar(cliente.idCliente).subscribe({
      next: () => {
        this.mensaje = 'Cliente eliminado correctamente.';
        this.cargar();
      },
      error: error => {
        if (error.status === 409) {
          this.mensaje =
            'No se puede eliminar este cliente porque tiene reservas registradas.';
        } else if (error.status === 404) {
          this.mensaje = 'El cliente ya no existe.';
        } else {
          this.mensaje = 'No se pudo eliminar el cliente. Inténtalo nuevamente.';
        }

        this.cdr.markForCheck();
      }
    });
  }
}
