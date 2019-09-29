package br.com.luisrjaeger.airwatch.helper

import br.com.luisrjaeger.airwatch.model.response.Search

class AppFilterHelper {

    static List<Search.Application> filterBundle(List<Search.Application> apps, Integer groupId) {
        return apps?.findAll { it.LocationGroupId == groupId } ?: [ ]
    }

    static List<Search.Application> filterVersion(List<Search.Application> apps, String version, Integer groupId) {
        return filterBundle(apps, groupId).findAll { it.AppVersion == version }
    }

    static Search.Application getVersion(List<Search.Application> apps, String version, Integer groupId) {
        return filterVersion(apps, version, groupId)?.first()
    }

    static boolean existVersion(List<Search.Application> apps, String version, Integer groupId) {
        return apps?.any { it.AppVersion == version && it.LocationGroupId == groupId } ?: false
    }

}
