
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private router = inject(Router);
  private authService = inject(AuthService);

  mostrarMenu = false;
  esAdmin = false;

  ngOnInit(): void {
    this.actualizarLayout();

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => this.actualizarLayout());
  }

  actualizarLayout(): void {
    const ruta = this.router.url;

    this.mostrarMenu = ruta.startsWith('/admin') || ruta.startsWith('/recepcion');
    this.esAdmin = ruta.startsWith('/admin');
  }

  cerrarSesion(): void {
    this.authService.cerrarSesion().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login'])
    });
  }
}
