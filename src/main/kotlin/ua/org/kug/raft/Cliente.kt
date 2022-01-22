package ua.org.kug.raft

import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.experimental.runBlocking
import mu.KotlinLogging

//Passar a porta e id na chamada dentro da main
class Cliente(val host: String = "localhost", val porta: Int, val id: Int) {

    val log = KotlinLogging.logger("Cliente")

    val raft = raftInstance()


    //Sincronizando informações para replicação de logs
    private fun raftInstance(): RaftGrpcKt.RaftKtStub {
        val servidor = ManagedChannelBuilder.forAddress(host, porta)
                .usePlaintext()
                .build()

        val Cliente = RaftGrpcKt.newStub(servidor)
        log.info { "Conectando porta: $porta" }
        return Cliente
    }

    suspend fun voto(termo: Int,
                            idCandidato: Int,
                            idUltimoLog: Int,
                            ultimoTermoLog: Int): ResponseVoteRPC {
        log.info { "Chamar votação - host: $host, porta: $porta, IdCandidato: $idCandidato, termo: $termo" }
        return raft.vote(
                RequestVoteRPC.newBuilder()
                        .setTerm(termo)
                        .setCandidateId(idCandidato)
                        .setLastLogIndex(idUltimoLog)
                        .setLastLogTerm(ultimoTermoLog)
                        .build()
        )
    }

    suspend fun anexar(
            idLider: Int,
            termo: Int,
            indexLogAnterior: Int,
            termoLogAnterior: Int,
            entradas: List<RequestAppendEntriesRPC.LogEntry>,
            liderCompromisso: Int
    ): ResponseAppendEntriesRPC {
        log.info { "Anexo da chamada - host: $host, porta: $porta, termo: $termo" }
        return raft.append(RequestAppendEntriesRPC.newBuilder()
                .setTerm(termo)
                .setLeaderId(idLider)
                .setPrevLogIndex(indexLogAnterior)
                .setPrevLogTerm(termoLogAnterior)
                .addAllEntries(entradas)
                .setLeaderCommit(liderCompromisso)
                .build()
        )
    }

}

fun main(args: Array<String>) = runBlocking {


 }



