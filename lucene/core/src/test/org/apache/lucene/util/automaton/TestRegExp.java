begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.util.automaton
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
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
name|LuceneTestCase
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInput
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutput
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
import|;
end_import
begin_class
DECL|class|TestRegExp
specifier|public
class|class
name|TestRegExp
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Simple smoke test for regular expression.    */
DECL|method|testSmoke
specifier|public
name|void
name|testSmoke
parameter_list|()
block|{
name|RegExp
name|r
init|=
operator|new
name|RegExp
argument_list|(
literal|"a(b+|c+)d"
argument_list|)
decl_stmt|;
name|Automaton
name|a
init|=
name|r
operator|.
name|toAutomaton
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|isDeterministic
argument_list|()
argument_list|)
expr_stmt|;
name|CharacterRunAutomaton
name|run
init|=
operator|new
name|CharacterRunAutomaton
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|run
operator|.
name|run
argument_list|(
literal|"abbbbbd"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|run
operator|.
name|run
argument_list|(
literal|"acd"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|run
operator|.
name|run
argument_list|(
literal|"ad"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Compiles a regular expression that is prohibitively expensive to    * determinize and expexts to catch an exception for it.    */
DECL|method|testDeterminizeTooManyStates
specifier|public
name|void
name|testDeterminizeTooManyStates
parameter_list|()
block|{
comment|// LUCENE-6046
name|String
name|source
init|=
literal|"[ac]*a[ac]{50,200}"
decl_stmt|;
name|TooComplexToDeterminizeException
name|expected
init|=
name|expectThrows
argument_list|(
name|TooComplexToDeterminizeException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|RegExp
argument_list|(
name|source
argument_list|)
operator|.
name|toAutomaton
argument_list|()
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-6713
DECL|method|testSerializeTooManyStatesToDeterminizeExc
specifier|public
name|void
name|testSerializeTooManyStatesToDeterminizeExc
parameter_list|()
throws|throws
name|Exception
block|{
comment|// LUCENE-6046
name|String
name|source
init|=
literal|"[ac]*a[ac]{50,200}"
decl_stmt|;
name|TooComplexToDeterminizeException
name|expected
init|=
name|expectThrows
argument_list|(
name|TooComplexToDeterminizeException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|RegExp
argument_list|(
name|source
argument_list|)
operator|.
name|toAutomaton
argument_list|()
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ObjectOutput
name|out
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|bos
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeObject
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|bos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|ByteArrayInputStream
name|bis
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|ObjectInput
name|in
init|=
operator|new
name|ObjectInputStream
argument_list|(
name|bis
argument_list|)
decl_stmt|;
name|TooComplexToDeterminizeException
name|e2
init|=
operator|(
name|TooComplexToDeterminizeException
operator|)
name|in
operator|.
name|readObject
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|e2
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-6046
DECL|method|testRepeatWithEmptyString
specifier|public
name|void
name|testRepeatWithEmptyString
parameter_list|()
throws|throws
name|Exception
block|{
name|Automaton
name|a
init|=
operator|new
name|RegExp
argument_list|(
literal|"[^y]*{1,2}"
argument_list|)
operator|.
name|toAutomaton
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
comment|// paranoia:
name|assertTrue
argument_list|(
name|a
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testRepeatWithEmptyLanguage
specifier|public
name|void
name|testRepeatWithEmptyLanguage
parameter_list|()
throws|throws
name|Exception
block|{
name|Automaton
name|a
init|=
operator|new
name|RegExp
argument_list|(
literal|"#*"
argument_list|)
operator|.
name|toAutomaton
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
comment|// paranoia:
name|assertTrue
argument_list|(
name|a
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|a
operator|=
operator|new
name|RegExp
argument_list|(
literal|"#+"
argument_list|)
operator|.
name|toAutomaton
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|a
operator|=
operator|new
name|RegExp
argument_list|(
literal|"#{2,10}"
argument_list|)
operator|.
name|toAutomaton
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|a
operator|=
operator|new
name|RegExp
argument_list|(
literal|"#?"
argument_list|)
operator|.
name|toAutomaton
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|a
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
