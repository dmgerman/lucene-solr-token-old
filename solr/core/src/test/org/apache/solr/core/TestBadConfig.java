begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package
begin_import
import|import
name|javax
operator|.
name|script
operator|.
name|ScriptEngineManager
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import
begin_class
DECL|class|TestBadConfig
specifier|public
class|class
name|TestBadConfig
extends|extends
name|AbstractBadConfigTestBase
block|{
DECL|method|testUnsetSysProperty
specifier|public
name|void
name|testUnsetSysProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|assertConfigs
argument_list|(
literal|"bad_solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|,
literal|"unset.sys.property"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleDirectoryFactories
specifier|public
name|void
name|testMultipleDirectoryFactories
parameter_list|()
throws|throws
name|Exception
block|{
name|assertConfigs
argument_list|(
literal|"bad-solrconfig-multiple-dirfactory.xml"
argument_list|,
literal|"schema12.xml"
argument_list|,
literal|"directoryFactory"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleIndexConfigs
specifier|public
name|void
name|testMultipleIndexConfigs
parameter_list|()
throws|throws
name|Exception
block|{
name|assertConfigs
argument_list|(
literal|"bad-solrconfig-multiple-indexconfigs.xml"
argument_list|,
literal|"schema12.xml"
argument_list|,
literal|"indexConfig"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleCFS
specifier|public
name|void
name|testMultipleCFS
parameter_list|()
throws|throws
name|Exception
block|{
name|assertConfigs
argument_list|(
literal|"bad-solrconfig-multiple-cfs.xml"
argument_list|,
literal|"schema12.xml"
argument_list|,
literal|"useCompoundFile"
argument_list|)
expr_stmt|;
block|}
DECL|method|testUpdateLogButNoVersionField
specifier|public
name|void
name|testUpdateLogButNoVersionField
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"enable.update.log"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertConfigs
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|,
literal|"_version_"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"enable.update.log"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testBogusScriptEngine
specifier|public
name|void
name|testBogusScriptEngine
parameter_list|()
throws|throws
name|Exception
block|{
comment|// sanity check
name|Assume
operator|.
name|assumeTrue
argument_list|(
literal|null
operator|==
operator|(
operator|new
name|ScriptEngineManager
argument_list|()
operator|)
operator|.
name|getEngineByName
argument_list|(
literal|"giberish"
argument_list|)
argument_list|)
expr_stmt|;
name|assertConfigs
argument_list|(
literal|"bad-solrconfig-bogus-scriptengine-name.xml"
argument_list|,
literal|"schema.xml"
argument_list|,
literal|"giberish"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMissingScriptFile
specifier|public
name|void
name|testMissingScriptFile
parameter_list|()
throws|throws
name|Exception
block|{
comment|// sanity check
name|Assume
operator|.
name|assumeNotNull
argument_list|(
operator|(
operator|new
name|ScriptEngineManager
argument_list|()
operator|)
operator|.
name|getEngineByExtension
argument_list|(
literal|"js"
argument_list|)
argument_list|)
expr_stmt|;
name|assertConfigs
argument_list|(
literal|"bad-solrconfig-missing-scriptfile.xml"
argument_list|,
literal|"schema.xml"
argument_list|,
literal|"a-file-name-that-does-not-exist.js"
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidScriptFile
specifier|public
name|void
name|testInvalidScriptFile
parameter_list|()
throws|throws
name|Exception
block|{
comment|// sanity check
name|Assume
operator|.
name|assumeNotNull
argument_list|(
operator|(
operator|new
name|ScriptEngineManager
argument_list|()
operator|)
operator|.
name|getEngineByName
argument_list|(
literal|"javascript"
argument_list|)
argument_list|)
expr_stmt|;
name|assertConfigs
argument_list|(
literal|"bad-solrconfig-invalid-scriptfile.xml"
argument_list|,
literal|"schema.xml"
argument_list|,
literal|"currency.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|testBogusMergePolicy
specifier|public
name|void
name|testBogusMergePolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|assertConfigs
argument_list|(
literal|"bad-mp-solrconfig.xml"
argument_list|,
literal|"schema-minimal.xml"
argument_list|,
literal|"DummyMergePolicy"
argument_list|)
expr_stmt|;
block|}
DECL|method|testSchemaMutableButNotManaged
specifier|public
name|void
name|testSchemaMutableButNotManaged
parameter_list|()
throws|throws
name|Exception
block|{
name|assertConfigs
argument_list|(
literal|"bad-solrconfig-schema-mutable-but-not-managed.xml"
argument_list|,
literal|"schema-minimal.xml"
argument_list|,
literal|"Unexpected arg(s): {mutable=false,managedSchemaResourceName=schema.xml}"
argument_list|)
expr_stmt|;
block|}
DECL|method|testManagedSchemaCannotBeNamedSchemaDotXml
specifier|public
name|void
name|testManagedSchemaCannotBeNamedSchemaDotXml
parameter_list|()
throws|throws
name|Exception
block|{
name|assertConfigs
argument_list|(
literal|"bad-solrconfig-managed-schema-named-schema.xml.xml"
argument_list|,
literal|"schema-minimal.xml"
argument_list|,
literal|"managedSchemaResourceName can't be 'schema.xml'"
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnknownSchemaAttribute
specifier|public
name|void
name|testUnknownSchemaAttribute
parameter_list|()
throws|throws
name|Exception
block|{
name|assertConfigs
argument_list|(
literal|"bad-solrconfig-unexpected-schema-attribute.xml"
argument_list|,
literal|"schema-minimal.xml"
argument_list|,
literal|"Unexpected arg(s): {bogusParam=bogusValue}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
