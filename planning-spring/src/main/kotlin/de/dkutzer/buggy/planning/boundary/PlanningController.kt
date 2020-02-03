package de.dkutzer.buggy.planning.boundary

import de.dkutzer.buggy.planning.control.PlanningService
import de.dkutzer.buggy.planning.entity.PlanningDto
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(value = ["/planning"], produces = [MediaType.APPLICATION_JSON_VALUE])
@RestController
class PlanningController(val planningService: PlanningService) {

    @GetMapping
    fun doPlanning(): PlanningDto = planningService.doPlanning()
}