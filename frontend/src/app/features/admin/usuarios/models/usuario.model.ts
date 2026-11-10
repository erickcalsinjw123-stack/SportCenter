export interface Usuario {
  idUsuario: number;
  nombreUsuario: string;
  rol: 'ADMIN' | 'RECEPCIONISTA';
  estado: 'ACTIVO' | 'INACTIVO';
  fechaCreacion: string;
}

export interface UsuarioRequest {
  nombreUsuario: string;
  contrasena?: string;
  rol: 'ADMIN' | 'RECEPCIONISTA';
  estado: 'ACTIVO' | 'INACTIVO';
}