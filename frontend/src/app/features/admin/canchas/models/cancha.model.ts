export interface Cancha {
  idCancha: number;
  nombre: string;
  precioHora: number;
  estado: 'DISPONIBLE' | 'MANTENIMIENTO';
}

export interface CanchaRequest {
  nombre: string;
  precioHora: number;
  estado: 'DISPONIBLE' | 'MANTENIMIENTO';
}