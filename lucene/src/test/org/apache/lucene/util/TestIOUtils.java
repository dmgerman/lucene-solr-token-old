begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import
begin_class
DECL|class|TestIOUtils
specifier|public
class|class
name|TestIOUtils
extends|extends
name|LuceneTestCase
block|{
DECL|class|BrokenCloseable
specifier|static
specifier|final
class|class
name|BrokenCloseable
implements|implements
name|Closeable
block|{
DECL|field|i
specifier|final
name|int
name|i
decl_stmt|;
DECL|method|BrokenCloseable
specifier|public
name|BrokenCloseable
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|this
operator|.
name|i
operator|=
name|i
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"TEST-IO-EXCEPTION-"
operator|+
name|i
argument_list|)
throw|;
block|}
block|}
DECL|class|TestException
specifier|static
specifier|final
class|class
name|TestException
extends|extends
name|Exception
block|{
DECL|method|TestException
specifier|public
name|TestException
parameter_list|()
block|{
name|super
argument_list|(
literal|"BASE-EXCEPTION"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSuppressedExceptions
specifier|public
name|void
name|testSuppressedExceptions
parameter_list|()
block|{
name|boolean
name|isJava7
init|=
literal|true
decl_stmt|;
try|try
block|{
comment|// this class only exists in Java 7:
name|Class
operator|.
name|forName
argument_list|(
literal|"java.lang.AutoCloseable"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
name|isJava7
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isJava7
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: TestIOUtils.testSuppressedExceptions: Full test coverage only with Java 7, as suppressed exception recording is not supported before."
argument_list|)
expr_stmt|;
block|}
comment|// test with prior exception
try|try
block|{
specifier|final
name|TestException
name|t
init|=
operator|new
name|TestException
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|t
argument_list|,
operator|new
name|BrokenCloseable
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|BrokenCloseable
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TestException
name|e1
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"BASE-EXCEPTION"
argument_list|,
name|e1
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|e1
operator|.
name|printStackTrace
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
specifier|final
name|String
name|trace
init|=
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TestIOUtils.testSuppressedExceptions: Thrown Exception stack trace:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|trace
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isJava7
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Stack trace does not contain first suppressed Exception: "
operator|+
name|trace
argument_list|,
name|trace
operator|.
name|contains
argument_list|(
literal|"java.io.IOException: TEST-IO-EXCEPTION-1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Stack trace does not contain second suppressed Exception: "
operator|+
name|trace
argument_list|,
name|trace
operator|.
name|contains
argument_list|(
literal|"java.io.IOException: TEST-IO-EXCEPTION-2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e2
parameter_list|)
block|{
name|fail
argument_list|(
literal|"IOException should not be thrown here"
argument_list|)
expr_stmt|;
block|}
comment|// test without prior exception
try|try
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
operator|(
name|TestException
operator|)
literal|null
argument_list|,
operator|new
name|BrokenCloseable
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|BrokenCloseable
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TestException
name|e1
parameter_list|)
block|{
name|fail
argument_list|(
literal|"TestException should not be thrown here"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e2
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"TEST-IO-EXCEPTION-1"
argument_list|,
name|e2
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|e2
operator|.
name|printStackTrace
argument_list|(
name|pw
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
specifier|final
name|String
name|trace
init|=
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TestIOUtils.testSuppressedExceptions: Thrown Exception stack trace:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|trace
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isJava7
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Stack trace does not contain suppressed Exception: "
operator|+
name|trace
argument_list|,
name|trace
operator|.
name|contains
argument_list|(
literal|"java.io.IOException: TEST-IO-EXCEPTION-2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
