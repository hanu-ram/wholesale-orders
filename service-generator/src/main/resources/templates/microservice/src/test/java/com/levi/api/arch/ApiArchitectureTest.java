package com.levi.allocation.arch;

import com.levi.allocation.Application;
import com.levi.allocation.dtos.Entity;
import com.levi.common.arch.AbstractArchitectureTests;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

class ApiArchitectureTest extends AbstractArchitectureTests {

    public ApiArchitectureTest() {
        super(new ClassFileImporter()
                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                        .importPackagesOf(Application.class),
                new String[]{Entity.class.getPackage().getName()},
                "com.levi.api.domain",
                Application.class.getPackage().getName(),
                "com.levi.api.(*)..",
                new String[]{"Dto"},
                new String[]{"Util", "Utils"});

    }
}