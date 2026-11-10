export type EstadoPago =
  | 'PENDIENTE'
  | 'COMPLETADO'
  | 'RECHAZADO'
  | 'REEMBOLSADO'
  | 'CANCELADO';

export type MetodoPago =
  | 'BCP'
  | 'YAPE'
  | 'PLIN'
  | 'INTERBANK'
  | 'EFECTIVO'
  | 'TARJETA'
  | 'BBVA'
  | 'SCOTIABANK'
  | 'OTRO';

export interface Pago {
  idPago: number;
  idReserva: number;
  nombreCliente: string;
  nombreCancha: string;
  monto: number;
  fechaPago: string | null;
  metodo: string | null;
  estado: EstadoPago;
}

export interface PagoRequest {
  metodo: MetodoPago;
  otroMetodo?: string;
}