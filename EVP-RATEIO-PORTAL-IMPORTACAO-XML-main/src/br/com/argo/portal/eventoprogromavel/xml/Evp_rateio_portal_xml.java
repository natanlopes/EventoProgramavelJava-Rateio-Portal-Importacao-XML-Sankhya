package br.com.argo.portal.eventoprogromavel.xml;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;

import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
public class Evp_rateio_portal_xml implements  EventoProgramavelJava {



	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

		
	}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

		
	}
	

	public void beforeUpdate(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

		DynamicVO vo = (DynamicVO) event.getVo();
	

		// JapeWrapper importXml = JapeFactory.dao("ImportacaoXMLNotas");

		BigDecimal nUnico = vo.asBigDecimal("NUNOTA");
     
		BigDecimal nUnicoEncontrado = buscarNunico(nUnico);
 
		if (nUnicoEncontrado.compareTo(BigDecimal.ZERO) != 0) {
//	            ctx.setMensagemRetorno("A operação não pode ser executada pois já existe rateio para o NUNOTA: " + nUnico + ".");
		} else {
			List<Map<String, Object>> rateioOrigem = buscarNunfi(nUnico);
			if (!rateioOrigem.isEmpty()) {
				// Rateio encontrado na nota de origem
			

				// Copiando os dados de rateio para a nova nota
				for (Map<String, Object> rateioData : rateioOrigem) {
					rateioData.put("NUFIN", nUnico); // Definindo o NUNOTA da nova nota como NUFIM
					insertInfoRteio(rateioData); // Inserindo o rateio na nova nota

				}
				atualizaStatusPedido(nUnico);
			} else {
				 
			}
		}
	}
	



	public void atualizaStatusPedido(BigDecimal nrUnico) {
		// TODO Auto-generated method stub

		SessionHandle hnd = null;
		try {
			hnd = JapeSession.open();

			JapeFactory.dao(DynamicEntityNames.CABECALHO_NOTA)
					.prepareToUpdateByPK(nrUnico)
					.set("RATEADO", "S")
					.update();


			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JapeSession.close(hnd);
		}

	}
	private BigDecimal buscarNunico(BigDecimal nUnico) throws Exception {
		JapeSession.SessionHandle hnd = null;
		JdbcWrapper jdbc = null;
		NativeSql query = null;
		ResultSet rset = null;
		BigDecimal numUnico = BigDecimal.ZERO;

		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			query = new NativeSql(jdbc);
			query.appendSql("SELECT NUFIN FROM TGFRAT \r\n"
					+ "WHERE NUFIN = :NUFIN");
			query.setNamedParameter("NUFIN", nUnico);
			rset = query.executeQuery();

			while (rset.next()) {
				numUnico = rset.getBigDecimal("NUFIN");
				return numUnico;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao executar a busca de informações: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
			JdbcWrapper.closeSession(jdbc);
			NativeSql.releaseResources(query);
		}

		return numUnico;
	}
	private List<Map<String, Object>> buscarNunfi(BigDecimal nuNota) throws Exception {
		JapeSession.SessionHandle hnd = null;
		JdbcWrapper jdbc = null;
		NativeSql query = null;
		ResultSet rset = null;
		
		 List<Map<String, Object>> resultList = new ArrayList<>();
		try {
			hnd = JapeSession.open();
			hnd.setCanTimeout(false);
			hnd.setFindersMaxRows(-1);
			EntityFacade entity = EntityFacadeFactory.getDWFFacade();
			jdbc = entity.getJdbcWrapper();
			jdbc.openSession();
			query = new NativeSql(jdbc);
			query.appendSql("SELECT\r\n"
					+ "					RAT.ORIGEM AS ORIGEM,\r\n"
					+ "				\r\n"
					+ "					RAT.NUFIN AS NUFIN,\r\n"
					+ "				\r\n"
					+ "					RAT.CODNAT AS  CODNAT,\r\n"
					+ "				\r\n"
					+ "					RAT.CODCENCUS AS  CODCENCUS,\r\n"
					+ "				\r\n"
					+ "					RAT.CODPROJ AS  CODPROJ ,\r\n"
					+ "				\r\n"
					+ "					RAT.PERCRATEIO AS  PERCRATEIO,\r\n"
					+ "				\r\n"
					+ "					ROUND(SUM((PED.VLRUNIT - (PED.VLRDESC / PED.QTDNEG)) * VAR.QTDATENDIDA * RAT.PERCRATEIO / 100), 10) AS VALOR,\r\n"
					+ "				\r\n"
					+ "				    RAT.CODCTACTB AS  CODCTACTB,\r\n"
					+ "				\r\n"
					+ "					RAT.NUMCONTRATO  AS NUMCONTRATO,\r\n"
					+ "				\r\n"
					+ "					RAT.DIGITADO AS  DIGITADO,\r\n"
					+ "					RAT.CODSITE AS CODSITE,\r\n"
					+ "				\r\n"
					+ "					RAT.CODPARC AS CODPARC,\r\n"
					+ "				\r\n"
					+ "				RAT.CODUSU AS  CODUSU,\r\n"
					+ "				\r\n"
					+ "				RAT.DTALTER AS  DTALTER\r\n"
					+ "				FROM\r\n"
					+ "					TGFVAR VAR , 	TGFITE PED , 	TGFRAT RAT , 	TGFCAB CAB\r\n"
					+ "				WHERE 	VAR.NUNOTA = :NUNOTA\r\n"
					+ "					AND VAR.NUNOTA <> VAR.NUNOTAORIG\r\n"
					+ "					AND PED.NUNOTA = VAR.NUNOTAORIG\r\n"
					+ "					AND PED.SEQUENCIA = VAR.SEQUENCIAORIG\r\n"
					+ "					AND RAT.NUFIN = VAR.NUNOTAORIG\r\n"
					+ "					AND CAB.NUNOTA = VAR.NUNOTAORIG\r\n"
					+ "					AND RAT.ORIGEM = 'E'\r\n"
					+ "				--	AND VAR.QTDATENDIDA <> 0\r\n"
					+ "				--	AND PED.QTDNEG <> 0\r\n"
					+ "				GROUP BY\r\n"
					+ "					RAT.ORIGEM,\r\n"
					+ "					RAT.NUFIN,\r\n"
					+ "					RAT.CODNAT,\r\n"
					+ "					RAT.CODCENCUS,\r\n"
					+ "					RAT.CODPROJ,\r\n"
					+ "					RAT.PERCRATEIO,\r\n"
					+ "					RAT.CODCTACTB,\r\n"
					+ "					RAT.NUMCONTRATO,\r\n"
					+ "					RAT.DIGITADO,\r\n"
					+ "					RAT.CODSITE,\r\n"
					+ "					RAT.CODPARC,\r\n"
					+ "					RAT.CODUSU,\r\n"
					+ "					RAT.DTALTER");
			query.setNamedParameter("NUNOTA", nuNota);
			rset = query.executeQuery();

			while (rset.next()) {
				 Map<String, Object> record = new HashMap<>();
				 record.put("ORIGEM", rset.getString("ORIGEM"));
		         record.put("NUFIN", rset.getBigDecimal("NUFIN"));
		         record.put("CODNAT", rset.getBigDecimal("CODNAT"));
		         record.put("CODCENCUS", rset.getBigDecimal("CODCENCUS"));
		         record.put("CODPROJ", rset.getBigDecimal("CODPROJ"));
		         record.put("PERCRATEIO", rset.getBigDecimal("PERCRATEIO"));
		         record.put("VALOR", rset.getBigDecimal("VALOR"));
		         record.put("CODCTACTB", rset.getBigDecimal("CODCTACTB"));
		         record.put("NUMCONTRATO", rset.getBigDecimal("NUMCONTRATO"));
		         record.put("DIGITADO", rset.getString("DIGITADO"));
		         record.put("CODSITE", rset.getBigDecimal("CODSITE"));
		         record.put("CODPARC", rset.getBigDecimal("CODPARC"));
		         record.put("CODUSU", rset.getBigDecimal("CODUSU"));
		         record.put("DTALTER", rset.getTimestamp("DTALTER"));
		         resultList.add(record);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Erro ao executar a busca de informações do  buscarNunfi: " + e.getMessage());
		} finally {
			JapeSession.close(hnd);
			JdbcWrapper.closeSession(jdbc);
			NativeSql.releaseResources(query);
		}

		 return resultList;
	}
	public void insertInfoRteio(Map<String, Object> rateioData) throws Exception {
        final EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
        final JdbcWrapper jdbc = entityFacade.getJdbcWrapper();
        PreparedStatement pstmt = null;
        jdbc.openSession();
        String sqlUpdate = "INSERT INTO TGFRAT (ORIGEM,NUFIN,CODNAT,CODCENCUS,CODPROJ,PERCRATEIO,CODCTACTB,NUMCONTRATO,DIGITADO,CODSITE,CODPARC,CODUSU,DTALTER) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        pstmt = jdbc.getPreparedStatement(sqlUpdate);
        pstmt.setString(1, (String) rateioData.get("ORIGEM"));
        pstmt.setBigDecimal(2, (BigDecimal) rateioData.get("NUFIN"));
        pstmt.setBigDecimal(3, (BigDecimal) rateioData.get("CODNAT"));
        pstmt.setBigDecimal(4, (BigDecimal) rateioData.get("CODCENCUS"));
        pstmt.setBigDecimal(5, (BigDecimal) rateioData.get("CODPROJ"));
        pstmt.setBigDecimal(6, (BigDecimal) rateioData.get("PERCRATEIO"));
        pstmt.setBigDecimal(7, (BigDecimal) rateioData.get("CODCTACTB"));
        pstmt.setBigDecimal(8, (BigDecimal) rateioData.get("NUMCONTRATO"));
        pstmt.setString(9, (String) rateioData.get("DIGITADO"));
        pstmt.setBigDecimal(10, (BigDecimal) rateioData.get("CODSITE"));
        pstmt.setBigDecimal(11, (BigDecimal) rateioData.get("CODPARC"));
        pstmt.setBigDecimal(12, (BigDecimal) rateioData.get("CODUSU"));
        pstmt.setTimestamp(13, (Timestamp) rateioData.get("DTALTER"));
        pstmt.executeUpdate();
        try {
            if (pstmt != null) {
                pstmt.close();
            }
            if (jdbc != null) {
                jdbc.closeSession();
            }
        }
        catch (Exception se) {
            se.printStackTrace();
        }
    }

}
