package de.dkutzer.buggy.developer.boundary

import de.dkutzer.buggy.developer.entity.Developer
import io.micrometer.core.annotation.Timed
import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@Timed(value = "de.dkutzer.buggy.changes" )
class ChangesController(val javers: Javers) {

    @GetMapping("/developers/changes")
    fun getDevelopersChanges(): String =
        javers.jsonConverter.toJson(javers.findChanges(QueryBuilder.byClass(Developer::class.java).build()))


}