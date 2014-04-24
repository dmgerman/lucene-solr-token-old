begin_unit
begin_package
DECL|package|org.apache.lucene.util.junitcompat
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|junitcompat
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|SuppressSysoutChecks
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|*
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|RuleChain
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|JUnitCore
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Result
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedContext
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|rules
operator|.
name|SystemPropertiesRestoreRule
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|TestSameRandomnessLocalePassedOrNot
specifier|public
class|class
name|TestSameRandomnessLocalePassedOrNot
extends|extends
name|WithNestedTests
block|{
annotation|@
name|ClassRule
DECL|field|solrClassRules
specifier|public
specifier|static
name|TestRule
name|solrClassRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|solrTestRules
specifier|public
name|TestRule
name|solrTestRules
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
operator|new
name|SystemPropertiesRestoreRule
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|TestSameRandomnessLocalePassedOrNot
specifier|public
name|TestSameRandomnessLocalePassedOrNot
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressSysoutChecks
argument_list|(
name|bugUrl
operator|=
literal|"Expected."
argument_list|)
DECL|class|Nested
specifier|public
specifier|static
class|class
name|Nested
extends|extends
name|WithNestedTests
operator|.
name|AbstractNestedTest
block|{
DECL|field|pickString
specifier|public
specifier|static
name|String
name|pickString
decl_stmt|;
DECL|field|defaultLocale
specifier|public
specifier|static
name|Locale
name|defaultLocale
decl_stmt|;
DECL|field|defaultTimeZone
specifier|public
specifier|static
name|TimeZone
name|defaultTimeZone
decl_stmt|;
DECL|field|seed
specifier|public
specifier|static
name|String
name|seed
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
block|{
name|seed
operator|=
name|RandomizedContext
operator|.
name|current
argument_list|()
operator|.
name|getRunnerSeedAsString
argument_list|()
expr_stmt|;
name|Random
name|rnd
init|=
name|random
argument_list|()
decl_stmt|;
name|pickString
operator|=
name|TestUtil
operator|.
name|randomSimpleString
argument_list|(
name|rnd
argument_list|)
expr_stmt|;
name|defaultLocale
operator|=
name|Locale
operator|.
name|getDefault
argument_list|()
expr_stmt|;
name|defaultTimeZone
operator|=
name|TimeZone
operator|.
name|getDefault
argument_list|()
expr_stmt|;
block|}
DECL|method|testPassed
specifier|public
name|void
name|testPassed
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Picked locale: "
operator|+
name|defaultLocale
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Picked timezone: "
operator|+
name|defaultTimeZone
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSetupWithoutLocale
specifier|public
name|void
name|testSetupWithoutLocale
parameter_list|()
block|{
name|Result
name|runClasses
init|=
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|Nested
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runClasses
operator|.
name|getFailureCount
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|s1
init|=
name|Nested
operator|.
name|pickString
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.seed"
argument_list|,
name|Nested
operator|.
name|seed
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.timezone"
argument_list|,
name|Nested
operator|.
name|defaultTimeZone
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tests.locale"
argument_list|,
name|Nested
operator|.
name|defaultLocale
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|Nested
operator|.
name|class
argument_list|)
expr_stmt|;
name|String
name|s2
init|=
name|Nested
operator|.
name|pickString
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s1
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
