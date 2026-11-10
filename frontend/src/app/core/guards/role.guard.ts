import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';

import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const rolesPermitidos = route.data['roles'] as string[];

  return authService.obtenerSesion().pipe(
    map((respuesta) => {
      return rolesPermitidos.includes(respuesta.rol)
        ? true
        : router.createUrlTree(['/login']);
    }),
    catchError(() => of(router.createUrlTree(['/login'])))
  );
};