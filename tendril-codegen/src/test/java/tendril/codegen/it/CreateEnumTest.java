/*
 * Copyright 2025 Jaroslav Bosak
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/license/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tendril.codegen.it;

import javax.annotation.processing.Generated;
import javax.lang.model.type.TypeKind;

import org.junit.jupiter.api.Test;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.value.JValueFactory;
import tendril.test.assertions.matchers.MultiLineStringMatcher;

/**
 * Test case to ensure that enum classes can be generated
 */
public class CreateEnumTest {

    /**
     * Verify that the empty enum generates properly
     */
    @Test
    public void testCreateEmptyEnum() {
        JClass cls = ClassBuilder.forEnum(new ClassType("a.b.c.d.E")).setVisibility(VisibilityType.PUBLIC).build();
        
        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package a.b.c.d;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("public enum E {");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(cls.generateCode());
    }
    
    /**
     * Verify that a single enum entry can be added
     */
    @Test
    public void testCreateWithSingleEntryOnly() {
        JClass cls = ClassBuilder.forEnum(new ClassType("a.b.c.d.E")).setVisibility(VisibilityType.PUBLIC)
                .buildEnumeration("ABC").build()
                .build();
        
        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package a.b.c.d;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("public enum E {");
        matcher.eq("");
        matcher.eq("    ABC;");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(cls.generateCode());
    }
    
    /**
     * Verify that a multiple enum entries can be added
     */
    @Test
    public void testCreateWithMultipleEntries() {
        JClass cls = ClassBuilder.forEnum(new ClassType("a.b.c.d.E")).setVisibility(VisibilityType.PUBLIC)
                .buildEnumeration("ABC").build()
                .buildEnumeration("DEF").build()
                .buildEnumeration("GHI").build()
                .buildEnumeration("JKL").build()
                .build();
        
        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package a.b.c.d;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("public enum E {");
        matcher.eq("");
        matcher.eq("    ABC,");
        matcher.eq("    DEF,");
        matcher.eq("    GHI,");
        matcher.eq("    JKL;");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(cls.generateCode());
    }
    
    /**
     * Verify that a single enum entry with parameters can be added
     */
    @Test
    public void testCreateWithSingleEntryWithParameter() {
        JClass cls = ClassBuilder.forEnum(new ClassType("a.b.c.d.E")).setVisibility(VisibilityType.PUBLIC)
                .buildEnumeration("ABC").addParameter(JValueFactory.create("abc")).build()
                .build();
        
        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package a.b.c.d;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("public enum E {");
        matcher.eq("");
        matcher.eq("    ABC(\"abc\");");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(cls.generateCode());
    }
    
    /**
     * Verify that a multiple enum entries with parameters can be added
     */
    @Test
    public void testCreateWithMultipleEntriesWithParameters() {
        JClass cls = ClassBuilder.forEnum(new ClassType("a.b.c.d.E")).setVisibility(VisibilityType.PUBLIC)
                .buildEnumeration("ABC").addParameter(JValueFactory.create("abc")).build()
                .buildEnumeration("DEF").addParameter(JValueFactory.create(true)).addParameter(JValueFactory.create('f')).build()
                .buildEnumeration("GHI").build()
                .buildEnumeration("JKL").addParameter(JValueFactory.create(TypeKind.BYTE), JValueFactory.create(1.23), JValueFactory.create(321)).build()
                .build();
        
        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("package a.b.c.d;");
        matcher.eq("");
        matcher.eq("import " + Generated.class.getName() + ";");
        matcher.eq("import " + TypeKind.class.getName() + ";");
        matcher.eq("");
        matcher.regex("@" + Generated.class.getSimpleName() + "\\(.+\\)");
        matcher.eq("public enum E {");
        matcher.eq("");
        matcher.eq("    ABC(\"abc\"),");
        matcher.eq("    DEF(true, 'f'),");
        matcher.eq("    GHI,");
        matcher.eq("    JKL(TypeKind.BYTE, 1.23d, 321);");
        matcher.eq("");
        matcher.eq("}");
        matcher.match(cls.generateCode());
    }
}
