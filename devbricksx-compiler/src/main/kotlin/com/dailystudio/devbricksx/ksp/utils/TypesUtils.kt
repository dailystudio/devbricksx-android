package com.dailystudio.devbricksx.ksp.utils

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration

fun typeOfKotlinAny(resolver: Resolver): KSClassDeclaration? {
    return resolver.getClassDeclarationByName<Any>()
}

fun typeOf(resolver: Resolver, typeString: String): KSClassDeclaration? {
    return resolver.getClassDeclarationByName(typeString)
}
