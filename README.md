# README: Integração CICS VT com DB2

## Visão Geral  
Este projeto explica, de forma simples e direta, como o CICS VT permite que suas aplicações legadas continuem usando arquivos VSAM enquanto, nos bastidores, todo o seu dado passa a viver em tabelas DB2. É a solução perfeita para migrar dados sem alterar uma linha de código COBOL ou PL I!

---

## Por que usar CICS VT?  
- **Zero alteração no código legado**  
  Seus programas continuam abrindo e lendo “VSAM”.  
- **Uma única fonte de verdade**  
  Todos os registros ficam apenas em DB2, eliminando problemas de sincronização.  
- **Manutenção simplificada**  
  Backup, recovery e DR concentram-se no DB2 — o “VSAM” é só fachada.  
- **Economia de esforço**  
  Sem retrabalho de recoding, você aproveita as ferramentas e skills de DB2 (SQL, performance, integridade).

---

## Componentes principais  

| Termo                      | O que é (em poucas palavras)                                                           |
|----------------------------|----------------------------------------------------------------------------------------|
| **CICS VT**                | “Motorzinho” que intercepta chamadas VSAM e as converte para SQL em DB2.               |
| **Load Library**           | Biblioteca binária do CICS VT (módulos prontos da IBM).                               |
| **New VT Driver Library**  | Biblioteca onde você coloca os módulos “driver” gerados para seu código legado.       |
| **DBRM**                   | Objeto pré-compilado que guarda um ou mais statements SQL — é gerado pelo precompilador. |
| **DB2 Package**            | Objeto “bindado” que agrupa DBRMs + configurações, pronto para ser executado pelo CICS. |
| **Control Tables**         | Tabelas DB2 que guardam o mapeamento e estatísticas do CICS VT.                       |
| **Application Tables**     | Tabelas DB2 que substituem cada arquivo VSAM migrado (seus dados de negócio).         |
| **Bancos de Dados**        | Recomenda-se ter um DB só para “controle” (VT) e outro para “aplicação” (seus dados).  |

---

## Passo a Passo Simplificado

1. **Instalar objetos do VT**  
   - Crie a *VT load library* e a *VT DBRM library* fornecidas pela IBM.  
   - Crie as *control tables* e *pacotes DB2* num banco isolado (ex.: `CICSVTDB`).

2. **Gerar seus drivers VT**  
   - Compile seus programas legados apontando para CICS VT em vez de VSAM.  
   - Os DBRMs resultantes vão para a *New VT driver DBRM library* e seus módulos para a *New VT driver library*.

3. **Configurar CICS**  
   - No **DFHCSD**, defina seus “arquivos VSAM” originais para apontar ao CICS VT.  
   - Atualize o **DFHRPL** e **STEPLIB** para incluir suas bibliotecas de drivers.  
   - Ajuste o **qualifier ISPF** para criar/edit datasets de tailoring com organização.

4. **Criar tabelas de aplicação**  
   - Para cada VSAM migrado, crie uma tabela no DB2 (ex.: `APPDB.CUSTOMERS`).  
   - Permita que sua aplicação leia/escreva nelas via CICS VT.

5. **Revisar segurança e performance**  
   - No banco de controle (`CICSVTDB`), restrinja INSERT/UPDATE apenas ao VT e DBAs.  
   - No banco de aplicação, dê permissões adequadas aos programas CICS e usuários.  
   - Separe tablespaces e buffer pools para workloads de controle e de negócios.

---

## Benefícios de um Ambiente Bem Estruturado

- **Segurança**: controles claros entre dados de controle e de negócios.  
- **Manutenção**: patches do VT e backup de dados independentes.  
- **Performance**: tuning específico para cada tipo de dado.  
- **Governança**: papéis e responsabilidades bem definidos (DBAs VT vs. equipes de aplicação).

---

## Próximos Passos

1. Revise os nomes de bibliotecas e qualificadores no seu ambiente.  
2. Solicite ao DBA a criação dos bancos e privilégios necessários.  
3. Planeje um piloto com um arquivo VSAM crítico para validar o fluxo.  
4. Gradualmente, migre outros arquivos VSAM seguindo o mesmo modelo.

---

> **Dica**: documente cada mapeamento VSAM→tabela DB2 e mantenha o tailoring PSB/DFHRPL sempre versionado em seu Git ou repositório de configuração.

---

Espero que este README ajude sua equipe a entender — de maneira clara e humanizada — como o CICS VT transforma chamadas antigas de VSAM em operações modernas no DB2, sem dor de cabeça no código! 🚀
