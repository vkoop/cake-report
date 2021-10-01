package de.vkoop.quark

import de.vkoop.quark.TransaktionTypes.Companion.FREEZER_STAKING_BONUS
import de.vkoop.quark.TransaktionTypes.Companion.LIQUIDITY_MINING_REWARD_BTC_DFI
import de.vkoop.quark.TransaktionTypes.Companion.STAKING_REWARD
import de.vkoop.quark.TransaktionTypes.Companion.TEN_YEARS_FREEZER_REWARD
import io.quarkus.runtime.QuarkusApplication
import java.io.File

class TransaktionTypes {
    companion object {
        const val FREEZER_STAKING_BONUS = "Freezer staking bonus"
        const val TEN_YEARS_FREEZER_REWARD = "10 years freezer reward"
        const val STAKING_REWARD ="Staking reward"
        const val LIQUIDITY_MINING_REWARD_BTC_DFI = "Liquidity mining reward BTC-DFI"
    }
}

class MyApp : QuarkusApplication {
    override fun run(vararg args: String?): Int {
        val fileName = args[0]!!
        val outputFile = args[1]!!

        val filterTypes = args.getOrNull(2)?.split(",")?.toList() ?: listOf()

        var table = File(fileName).readLines()
            .toList()
            .drop(1)
            .map {
                CsvLine.from(it)
            }

        if(filterTypes.isNotEmpty()){
            table = table.filter {
                filterTypes.contains(it.type)
            }
        }


        val byType = table.groupBy {
            it.type
        }

        val freezerRewards = byType.getOrDefault(FREEZER_STAKING_BONUS, emptyList())
        val freezer10Rewards = byType.getOrDefault(TEN_YEARS_FREEZER_REWARD, emptyList())


        val stakingRewards = byType.getOrDefault(STAKING_REWARD, emptyList())
        val liqudityMiningRewards = byType.getOrDefault(LIQUIDITY_MINING_REWARD_BTC_DFI, emptyList())
        val otherEntries = table - freezerRewards - stakingRewards - liqudityMiningRewards - freezer10Rewards


        val csvLines = processStaking(freezer10Rewards + freezerRewards + stakingRewards) + processLiquidtyMining(
            liqudityMiningRewards
        ) + processOther(otherEntries)


        File(outputFile).printWriter().use { out ->
            // add headerline
            out.println(File(fileName).readLines()[0])

            csvLines.forEach {
                out.println(it)
            }
        }

        return 0;
    }
}


private fun processOther(table: List<CsvLine>): List<String> {
    return table.map { it.toCsv() }
}

private fun processLiquidtyMining(table: List<CsvLine>): List<String> {

    val btcLines = table.filter {
        it.currency == "BTC"
    }
        .groupBy {
            it.date.toLocalDate()
        }
        .mapValues {
            it.value.reduce { accum, newV ->
                accum.copy(
                    amoumt = accum.amoumt + newV.amoumt,
                    amountFiat = accum.amountFiat + newV.amountFiat,
                )
            }
        }
        .values
        .map { it.copy(transactionId = "") }
        .map { it.toCsv() }

    val dfiLines = table.filter {
        it.currency == "DFI"
    }
        .groupBy {
            it.date.toLocalDate()
        }
        .mapValues {
            it.value.reduce { accum, newV ->
                accum.copy(
                    amoumt = accum.amoumt + newV.amoumt,
                    amountFiat = accum.amountFiat + newV.amountFiat,
                )
            }
        }
        .values
        .map { it.copy(transactionId = "") }
        .map { it.toCsv() }

    return dfiLines + btcLines

}


private fun processStaking(table: List<CsvLine>): List<String> {
    return table
        .groupBy {
            it.date.toLocalDate()
        }
        .mapValues {
            it.value.reduce { accum, newV ->
                accum.copy(
                    amoumt = accum.amoumt + newV.amoumt,
                    amountFiat = accum.amountFiat + newV.amountFiat,
                )
            }
        }
        .values
        .map { it.copy(type = STAKING_REWARD, transactionId = "") }
        .map { it.toCsv() }
}
