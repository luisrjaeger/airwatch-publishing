package br.com.luisrjaeger.airwatch.helper

import br.com.luisrjaeger.airwatch.model.response.Search

class AppFilterHelper {

    static List<Search.Application> filterVersion(List<Search.Application> apps, String version, Integer groupId) {
        return apps?.findAll { it.AppVersion == version && it.LocationGroupId == groupId } ?: [ ]
    }

    static boolean existVersion(List<Search.Application> apps, String version, Integer groupId) {
        return apps?.any { it.AppVersion == version && it.LocationGroupId == groupId } ?: [ ]
    }

}
