export interface Reserva {
  idReserva: number;
  idCliente: number;
  nombreCliente: string;
  idCancha: number;
  nombreCancha: string;
  idUsuario: number;
  deporte: string;
  fechaInicio: string;
  fechaFin: string;
  precioTotal: number;
  estado: 'CONFIRMADA' | 'CANCELADA';
  fechaRegistro: string;
}

export interface ReservaRequest {
  idCliente: number;
  idCancha: number;
  deporte: string;
  fechaInicio: string;
  fechaFin: string;
}