package de.econaxy.server.domain

import groovy.transform.EqualsAndHashCode

/**
 *
 */
@EqualsAndHashCode
class Path {
    private final static String WILDCARD = '*'
    List<Map<String, Class<? extends DomainTrait>>> parts = []
    String container

    protected Path(List<Map<String, Class<? extends DomainTrait>>> parts, String container = null) {
        this.parts = [] + parts
        this.container = container
    }

    Path(DomainTrait domain, String container = null) {
        List<Map<String, Class<? extends DomainTrait>>> reverseParts = []
        this.&fillPart.trampoline()(reverseParts, domain)
        parts = reverseParts.reverse()
        this.container = container
    }

    Path() {}

    Path rightShift(Class type) {
        parts << [uid: WILDCARD, type: type]
        return this
    }

    Path rightShift(String uid) {
        parts.last().uid = uid
        return this
    }

    Path rightShiftUnsigned(String container) {
        this.container = container
        return this
    }

    private void fillPart(List<Map<String, Class<? extends DomainTrait>>> parts, DomainTrait domain) {
        parts << [uid: domain.uid, type: domain.getClass()]
        if (domain.parent)
            fillPart(parts, domain.parent)
    }

    Path getParentPath() {
        if (container)
            new Path(parts)
        else
            new Path(parts.take(parts.size() - 1))
    }

    Path withContainer(String container) {
        return new Path(parts, container)
    }

    Class<? extends DomainTrait> getType() {
        return parts.last().type
    }

    Class<? extends DomainTrait> getUid() {
        return parts.last().uid
    }

    String toString() {
        parts.collect { "${it.type.simpleName}($it.uid)" }.join('.') + (container ? "@$container" : '')
    }

    boolean matches(Path filter) {
        // TODO: optimize to replace regex
        def containerFilter = filter.container ?: ''
        if (!(container ?: '').matches(containerFilter == WILDCARD ? '.*' : containerFilter))
            return false
        if (filter.parts.size() > parts.size()) return false
        for (int i = 0; i < parts.size(); i++) {
            if (parts[i].type != filter.parts[i].type)
                return false
            String uid = parts[i].uid
            String matcher = filter.parts[i].uid
            if (matcher != WILDCARD) {
                if (uid != matcher)
                    return false
            }
        }
        return true
    }
}
