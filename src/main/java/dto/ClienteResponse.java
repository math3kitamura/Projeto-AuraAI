package dto;

import domain.model.Cliente;

public record ClienteResponse(
        Long id,
        String nome,
        String cnpj,
        String segmento,
        String email
) {

    public static ClienteResponse de(Cliente cliente) {

        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCnpj(),
                cliente.getSegmento(),
                cliente.getEmail()
        );
    }
}
