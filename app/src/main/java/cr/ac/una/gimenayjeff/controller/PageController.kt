package cr.ac.una.gimenayjeff.controller

import cr.ac.una.gimenayjeff.clases.page
import cr.ac.una.gimenayjeff.service.PagesService

class PageController {
    var pagesService = PagesService()

    suspend fun  Buscar(terminoBusqueda: String):ArrayList<page>{
        return pagesService.apiWikiService.Buscar(terminoBusqueda).pages as ArrayList<page>
    }
}