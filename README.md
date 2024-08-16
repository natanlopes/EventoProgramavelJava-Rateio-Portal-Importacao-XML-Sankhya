 # Evento Sankhya - Copia de Rateio para Novas Notas


 
Este evento programável no ERP Sankhya realiza a cópia de informações de rateio de uma nota fiscal de origem para uma nova nota fiscal. Ele é executado antes da atualização de um registro DynamicVO e inclui as seguintes etapas principais:

## Verificação de Rateio Existente:

O código verifica se já existe um rateio associado ao NUNOTA (número único da nota) na nova nota (NUFIN). Se existir, a operação é interrompida.
## Busca de Rateio na Nota de Origem:

O código busca informações de rateio na nota de origem usando o NUNOTA.
## Cópia do Rateio para a Nova Nota:

Se informações de rateio forem encontradas na nota de origem, o código itera sobre esses dados e os insere na nova nota, atualizando o NUFIM (número único da nota fiscal de destino) para o NUNOTA da nova nota.
## Atualização do Status do Pedido:

Após a cópia do rateio, o status do pedido associado à nova nota é atualizado para indicar que o rateio foi realizado.
## Métodos Auxiliares:

- atualizaStatusPedido(BigDecimal nrUnico): Atualiza o campo RATEADO para "S" no cabeçalho da nota, indicando que o rateio foi feito.
- buscarNunico(BigDecimal nUnico): Busca um NUFIN (número único da nota fiscal) na tabela TGFRAT.
- buscarNunfi(BigDecimal nuNota): Busca informações de rateio na nota de origem com base no NUNOTA.
- insertInfoRteio(Map<String, Object> rateioData): Insere um novo registro de rateio na tabela TGFRAT.
## Observações:

- O código utiliza o framework Jape para interagir com o banco de dados do Sankhya.
- As funções de busca e inserção de dados utilizam consultas SQL nativas para maior flexibilidade.
- O código inclui tratamento de exceções para lidar com possíveis erros durante a execução.
## Requisitos:

ERP Sankhya

Framework Jape
## Como Utilizar:

- Compile o código Java.
- Configure o evento programável no Sankhya para chamar a classe e o método beforeUpdate.

- O evento será acionado automaticamente antes da atualização de um registro DynamicVO, realizando a cópia do rateio conforme descrito acima.
## Importante:

- Certifique-se de que as tabelas e campos mencionados no código estejam presentes no seu banco de dados Sankhya.
- Adapte o código se necessário para atender aos requisitos específicos do seu sistema.
- Realize testes completos antes de implementar o evento em produção.
