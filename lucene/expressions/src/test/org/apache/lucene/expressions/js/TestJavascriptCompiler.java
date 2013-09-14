begin_unit
begin_package
DECL|package|org.apache.lucene.expressions.js
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
operator|.
name|js
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
begin_class
DECL|class|TestJavascriptCompiler
specifier|public
class|class
name|TestJavascriptCompiler
extends|extends
name|LuceneTestCase
block|{
DECL|method|testValidCompiles
specifier|public
name|void
name|testValidCompiles
parameter_list|()
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"100"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"valid0+100"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"valid0+\n100"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"logn(2, 20+10-5.0)"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidCompiles
specifier|public
name|void
name|testInvalidCompiles
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"100 100"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|expected
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"7*/-8"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|expected
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"0y1234"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|expected
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"500EE"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|expected
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"500.5EE"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|expected
parameter_list|)
block|{
comment|// expected exception
block|}
block|}
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|expected
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"()"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|expected
parameter_list|)
block|{
comment|// expected exception
block|}
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"   \r\n   \n \t"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|expected
parameter_list|)
block|{
comment|// expected exception
block|}
block|}
DECL|method|testNull
specifier|public
name|void
name|testNull
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|expected
parameter_list|)
block|{
comment|// expected exception
block|}
block|}
DECL|method|testWrongArity
specifier|public
name|void
name|testWrongArity
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"tan()"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"arguments for method call"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"tan(1, 1)"
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"arguments for method call"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
