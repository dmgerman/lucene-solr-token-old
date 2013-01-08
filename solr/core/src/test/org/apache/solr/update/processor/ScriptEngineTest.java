begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package
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
name|Constants
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
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|script
operator|.
name|Invocable
import|;
end_import
begin_import
import|import
name|javax
operator|.
name|script
operator|.
name|ScriptEngine
import|;
end_import
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
name|javax
operator|.
name|script
operator|.
name|ScriptException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_comment
comment|/**  * Sanity tests basic functionality of {@link ScriptEngineManager} and   * {@link ScriptEngine} w/o excercising any Lucene specific code.  */
end_comment
begin_class
DECL|class|ScriptEngineTest
specifier|public
class|class
name|ScriptEngineTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|manager
specifier|private
name|ScriptEngineManager
name|manager
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeFalse
argument_list|(
literal|"https://twitter.com/UweSays/status/260487231880433664 / SOLR-4233: OS X bogusly starts AWT!"
argument_list|,
name|Constants
operator|.
name|MAC_OS_X
argument_list|)
expr_stmt|;
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
literal|"JavaScript"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|manager
operator|=
operator|new
name|ScriptEngineManager
argument_list|()
expr_stmt|;
block|}
DECL|method|testGetEngineByName
specifier|public
name|void
name|testGetEngineByName
parameter_list|()
block|{
name|ScriptEngine
name|engine
init|=
name|manager
operator|.
name|getEngineByName
argument_list|(
literal|"JavaScript"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|engine
argument_list|)
expr_stmt|;
name|engine
operator|=
name|manager
operator|.
name|getEngineByName
argument_list|(
literal|"DummyScript"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|engine
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetEngineByExtension
specifier|public
name|void
name|testGetEngineByExtension
parameter_list|()
block|{
name|ScriptEngine
name|engine
init|=
name|manager
operator|.
name|getEngineByExtension
argument_list|(
literal|"js"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|engine
argument_list|)
expr_stmt|;
name|engine
operator|=
name|manager
operator|.
name|getEngineByExtension
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|engine
argument_list|)
expr_stmt|;
block|}
DECL|method|testEvalText
specifier|public
name|void
name|testEvalText
parameter_list|()
throws|throws
name|ScriptException
throws|,
name|NoSuchMethodException
block|{
name|ScriptEngine
name|engine
init|=
name|manager
operator|.
name|getEngineByName
argument_list|(
literal|"JavaScript"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|engine
argument_list|)
expr_stmt|;
name|engine
operator|.
name|eval
argument_list|(
literal|"function add(a,b) { return a + b }"
argument_list|)
expr_stmt|;
name|Double
name|result
init|=
call|(
name|Double
call|)
argument_list|(
operator|(
name|Invocable
operator|)
name|engine
argument_list|)
operator|.
name|invokeFunction
argument_list|(
literal|"add"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEvalReader
specifier|public
name|void
name|testEvalReader
parameter_list|()
throws|throws
name|ScriptException
throws|,
name|NoSuchMethodException
block|{
name|ScriptEngine
name|engine
init|=
name|manager
operator|.
name|getEngineByName
argument_list|(
literal|"JavaScript"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|engine
argument_list|)
expr_stmt|;
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
literal|"function add(a,b) { return a + b }"
argument_list|)
decl_stmt|;
name|engine
operator|.
name|eval
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|Double
name|result
init|=
call|(
name|Double
call|)
argument_list|(
operator|(
name|Invocable
operator|)
name|engine
argument_list|)
operator|.
name|invokeFunction
argument_list|(
literal|"add"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPut
specifier|public
name|void
name|testPut
parameter_list|()
throws|throws
name|ScriptException
throws|,
name|NoSuchMethodException
block|{
name|manager
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ScriptEngine
name|engine
init|=
name|manager
operator|.
name|getEngineByName
argument_list|(
literal|"JavaScript"
argument_list|)
decl_stmt|;
name|engine
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|engine
argument_list|)
expr_stmt|;
name|engine
operator|.
name|eval
argument_list|(
literal|"function add() { return a + b }"
argument_list|)
expr_stmt|;
name|Double
name|result
init|=
call|(
name|Double
call|)
argument_list|(
operator|(
name|Invocable
operator|)
name|engine
argument_list|)
operator|.
name|invokeFunction
argument_list|(
literal|"add"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testJRuby
specifier|public
name|void
name|testJRuby
parameter_list|()
throws|throws
name|ScriptException
throws|,
name|NoSuchMethodException
block|{
comment|// Simply adding jruby.jar to Solr's lib/ directory gets this test passing
name|ScriptEngine
name|engine
init|=
name|manager
operator|.
name|getEngineByName
argument_list|(
literal|"jruby"
argument_list|)
decl_stmt|;
name|Assume
operator|.
name|assumeNotNull
argument_list|(
name|engine
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|engine
argument_list|)
expr_stmt|;
name|engine
operator|.
name|eval
argument_list|(
literal|"def add(a,b); a + b; end"
argument_list|)
expr_stmt|;
name|Long
name|result
init|=
call|(
name|Long
call|)
argument_list|(
operator|(
name|Invocable
operator|)
name|engine
argument_list|)
operator|.
name|invokeFunction
argument_list|(
literal|"add"
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
