import { Routes } from '@angular/router';

import { Login } from './features/auth/login/login';

import { Dashboard } from './features/admin/dashboard/dashboard';

import { CanchaList } from './features/admin/canchas/cancha-list/cancha-list';
import { CanchaForm } from './features/admin/canchas/cancha-form/cancha-form';

import { UsuarioList } from './features/admin/usuarios/usuario-list/usuario-list';
import { UsuarioForm } from './features/admin/usuarios/usuario-form/usuario-form';

import { ClienteList } from './features/recepcion/clientes/cliente-list/cliente-list';
import { ClienteForm } from './features/recepcion/clientes/cliente-form/cliente-form';

import { ReservaList } from './features/recepcion/reservas/reserva-list/reserva-list';
import { ReservaForm } from './features/recepcion/reservas/reserva-form/reserva-form';

import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

import { PagoList } from './features/recepcion/pagos/pago-list/pago-list';
import { PagoForm } from './features/recepcion/pagos/pago-form/pago-form';

export const routes: Routes = [

  {
    path: 'login',
    component: Login
  },

  {
    path: 'admin',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN'] },
    children: [

      {
        path: 'dashboard',
        component: Dashboard
      },

      {
        path: 'canchas',
        component: CanchaList
      },
      {
        path: 'canchas/nueva',
        component: CanchaForm
      },
      {
        path: 'canchas/editar/:id',
        component: CanchaForm
      },

      {
        path: 'usuarios',
        component: UsuarioList
      },
      {
        path: 'usuarios/nuevo',
        component: UsuarioForm
      },
      {
        path: 'usuarios/editar/:id',
        component: UsuarioForm
      },

      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }

    ]
  },

  {
    path: 'recepcion',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['RECEPCIONISTA'] },
    children: [

      {
        path: 'clientes',
        component: ClienteList
      },
      {
        path: 'clientes/nuevo',
        component: ClienteForm
      },
      {
        path: 'clientes/editar/:id',
        component: ClienteForm
      },

      {
        path: 'reservas',
        component: ReservaList
      },
      {
        path: 'reservas/nueva',
        component: ReservaForm
      },
      {
        path: 'reservas/editar/:id',
        component: ReservaForm
      },

      // Pagos
      {
        path: 'pagos',
        component: PagoList
      },
      {
        path: 'pagos/nuevo',
        component: PagoForm
      },

      {
        path: '',
        redirectTo: 'clientes',
        pathMatch: 'full'
      }

    ]
  },

  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },

  {
    path: '**',
    redirectTo: 'login'
  }

];