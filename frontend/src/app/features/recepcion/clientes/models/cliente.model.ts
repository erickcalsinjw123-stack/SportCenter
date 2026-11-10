export interface Cliente {
  idCliente: number;
  nombre: string;
  apellido: string;
  telefono: string;
  email: string;
}

export interface ClienteRequest {
  nombre: string;
  apellido: string;
  telefono: string;
  email: string;
}