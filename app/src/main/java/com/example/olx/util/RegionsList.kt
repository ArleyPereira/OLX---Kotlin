package com.example.olx.util

import com.example.olx.model.State

class RegionsList {

    companion object {
        fun getRegionsList(UFState: String): List<String> {
            val regions = mutableListOf<String>()

            regions.add("Todas as regiões")

            when (UFState) {
                "AC" -> regions.add("DDD 68 - Acre")
                "AL" -> regions.add("DDD 82 - Alagoas")
                "AP" -> regions.add("DDD 96 - Amapá")
                "AM" -> {
                    regions.add("DDD 92 - Amazonas")
                    regions.add("DDD 97 - Leste do Amazonas")
                }
                "BA" -> {
                    regions.add("DDD 71 - Salvador")
                    regions.add("DDD 73 - Sul da Bahia")
                    regions.add("DDD 74 - Juazeiro, Jacobina e região")
                    regions.add("DDD 75 - F. de Santana, Alagoinhas e região")
                    regions.add("DDD 77 - V da Conquista, Barreiras e região")
                }
                "CE" -> {
                    regions.add("DDD 85 - Fortaleza e região")
                    regions.add("DDD 88 - Juazeiro do Norte, Sobral e região")
                }
                "DF" -> regions.add("DDD 61 - Distrito Federal e região")
                "ES" -> {
                    regions.add("DDD 27 - Norte do Espírito Santo")
                    regions.add("DDD 28 - Sul do Espírito Santo")
                }
                "GO" -> {
                    regions.add("DDD 62 - Grande Goiânia e Anápolis")
                    regions.add("DDD 64 - Rio Verde, Caldas Novas e região")
                }
                "MA" -> {
                    regions.add("DDD 98 - Região de São Luíz")
                    regions.add("DDD 99 - Imperatriz, Caxias e região")
                }
                "MT" -> {
                    regions.add("DDD 65 - Cuiabá e região")
                    regions.add("DDD 66 - Rondonópolis, Sinop e região")
                }
                "MS" -> regions.add("DDD 67 - Mato Grosso do Sul")
                "MG" -> {
                    regions.add("DDD 31 - Belo Horizonte e região")
                    regions.add("DDD 32 - Juiz de Fora e região")
                    regions.add("DDD 33 - Gov. Valadares, T. Otoni e região")
                    regions.add("DDD 34 - Uberlândia, Uberaba e região")
                    regions.add("DDD 35 - Poços de Caldas e Varginha")
                    regions.add("DDD 37 - Divinópolis e região")
                    regions.add("DDD 38 - Mtes Claros, Diamantina e região")
                }
                "PA" -> {
                    regions.add("DDD 91 - Região de Belém")
                    regions.add("DDD 93 - Região de Santarém")
                    regions.add("DDD 94 - Região de Marabá")
                }
                "PB" -> regions.add("DDD 83 - Paraíba")
                "PR" -> {
                    regions.add("DDD 41 - Curitiba e região")
                    regions.add("DDD 42 - Pta Grossa, Guarapuava e região")
                    regions.add("DDD 43 - Londrina e região")
                    regions.add("DDD 44 - Maringá e região")
                    regions.add("DDD 45 - Foz do Iguaçu, Cascavel e região")
                    regions.add("DDD 46 - F. Beltrão e Pato Branco e região")
                }
                "PE" -> {
                    regions.add("DDD 81 - Grande Recife")
                    regions.add("DDD 87 - Petrolina, Garanhuns e região")
                }
                "PI" -> {
                    regions.add("DDD 86 - Teresina, Parnaíba e região")
                    regions.add("DDD 89 - Picos, Floriano e região")
                }
                "RJ" -> {
                    regions.add("DDD 21 - Rio de Janeiro e região")
                    regions.add("DDD 22 - Norte do State e Região dos Lagos")
                    regions.add("DDD 24 - Serra, Angra dos Reis e região")
                }
                "RN" -> regions.add("DDD 84 - Rio Grande do Norte")
                "RS" -> {
                    regions.add("DDD 51 - Porto Alegre e região")
                    regions.add("DDD 53 - Pelotas, Bagé, Rio Gde e região")
                    regions.add("DDD 54 - Caxias do Sul e região")
                    regions.add("DDD 55 - Sta Maria, Cruz Alta e região")
                }
                "RO" -> regions.add("DDD 69 - Rondônia")
                "RR" -> regions.add("DDD 96 - Roraima")
                "SC" -> {
                    regions.add("DDD 47 - Norte de Santa Catarina")
                    regions.add("DDD 48 - Florianópolis e região")
                    regions.add("DDD 49 - Oeste de Santa Catarina")
                }
                "SP" -> {
                    regions.add("DDD 11 - São Paulo e região")
                    regions.add("DDD 12 - V. do Paraíba e Litoral Norte")
                    regions.add("DDD 13 - Baixada Santista e Litoral Sul")
                    regions.add("DDD 14 - Bauru, Marília e região")
                    regions.add("DDD 15 - Sorocaba e região")
                    regions.add("DDD 16 - Ribeirão Preto e região")
                    regions.add("DDD 17 - S. José do Rio Preto e região")
                    regions.add("DDD 18 - Presidente Prudente e região")
                    regions.add("DDD 19 - Grande Campinas")
                }
                "SE" -> regions.add("DDD 79 - Sergipe")
                "TO" -> regions.add("DDD 63 - Tocantins")
            }

            return regions
        }

        fun getStatesList(): List<State> {
            val states = mutableListOf<State>()

            states.add(State(name = "Brasil", uf = ""))
            states.add(State(name = "Acre", uf = "AC"))
            states.add(State(name = "Alagoas", uf = "AL"))
            states.add(State(name = "Amapá", uf = "AP"))
            states.add(State(name = "Amazonas", uf = "AM"))
            states.add(State(name = "Bahia", uf = "BA"))
            states.add(State(name = "Ceará", uf = "CE"))
            states.add(State(name = "Distrito Federal", uf = "DF"))
            states.add(State(name = "Espírito Santo", uf = "ES"))
            states.add(State(name = "Goiás", uf = "GO"))
            states.add(State(name = "Maranhão", uf = "MA"))
            states.add(State(name = "Mato Grosso", uf = "MT"))
            states.add(State(name = "Mato Grosso do Sul", uf = "MS"))
            states.add(State(name = "Minas Gerais", uf = "MG"))
            states.add(State(name = "Pará", uf = "PA"))
            states.add(State(name = "Paraíba", uf = "PB"))
            states.add(State(name = "Paraná", uf = "PR"))
            states.add(State(name = "Pernambuco", uf = "PE"))
            states.add(State(name = "Piauí", uf = "PI"))
            states.add(State(name = "Rio de Janeiro", uf = "RJ"))
            states.add(State(name = "Rio Grande do Norte", uf = "RN"))
            states.add(State(name = "Rio Grande do Sul", uf = "RS"))
            states.add(State(name = "Rondônia", uf = "RO"))
            states.add(State(name = "Roraima", uf = "RR"))
            states.add(State(name = "Santa Catarina", uf = "SC"))
            states.add(State(name = "São Paulo", uf = "SP"))
            states.add(State(name = "Sergipe", uf = "SE"))
            states.add(State(name = "Tocantins", uf = "TO"))

            return states
        }
    }

}