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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|expressions
operator|.
name|Expression
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
begin_comment
comment|/** Tests customing the function map */
end_comment
begin_class
DECL|class|TestCustomFunctions
specifier|public
class|class
name|TestCustomFunctions
extends|extends
name|LuceneTestCase
block|{
DECL|field|DELTA
specifier|private
specifier|static
name|double
name|DELTA
init|=
literal|0.0000001
decl_stmt|;
comment|/** empty list of methods */
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"sqrt(20)"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unrecognized method"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** using the default map explicitly */
DECL|method|testDefaultList
specifier|public
name|void
name|testDefaultList
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
name|JavascriptCompiler
operator|.
name|DEFAULT_FUNCTIONS
decl_stmt|;
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"sqrt(20)"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|sqrt
argument_list|(
literal|20
argument_list|)
argument_list|,
name|expr
operator|.
name|evaluate
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
block|}
DECL|method|zeroArgMethod
specifier|public
specifier|static
name|double
name|zeroArgMethod
parameter_list|()
block|{
return|return
literal|5
return|;
block|}
comment|/** tests a method with no arguments */
DECL|method|testNoArgMethod
specifier|public
name|void
name|testNoArgMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"zeroArgMethod"
argument_list|)
argument_list|)
expr_stmt|;
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo()"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|expr
operator|.
name|evaluate
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
block|}
DECL|method|oneArgMethod
specifier|public
specifier|static
name|double
name|oneArgMethod
parameter_list|(
name|double
name|arg1
parameter_list|)
block|{
return|return
literal|3
operator|+
name|arg1
return|;
block|}
comment|/** tests a method with one arguments */
DECL|method|testOneArgMethod
specifier|public
name|void
name|testOneArgMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"oneArgMethod"
argument_list|,
name|double
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo(3)"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|expr
operator|.
name|evaluate
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
block|}
DECL|method|threeArgMethod
specifier|public
specifier|static
name|double
name|threeArgMethod
parameter_list|(
name|double
name|arg1
parameter_list|,
name|double
name|arg2
parameter_list|,
name|double
name|arg3
parameter_list|)
block|{
return|return
name|arg1
operator|+
name|arg2
operator|+
name|arg3
return|;
block|}
comment|/** tests a method with three arguments */
DECL|method|testThreeArgMethod
specifier|public
name|void
name|testThreeArgMethod
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"threeArgMethod"
argument_list|,
name|double
operator|.
name|class
argument_list|,
name|double
operator|.
name|class
argument_list|,
name|double
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo(3, 4, 5)"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|expr
operator|.
name|evaluate
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
block|}
comment|/** tests a map with 2 functions */
DECL|method|testTwoMethods
specifier|public
name|void
name|testTwoMethods
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"zeroArgMethod"
argument_list|)
argument_list|)
expr_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"bar"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"oneArgMethod"
argument_list|,
name|double
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Expression
name|expr
init|=
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo() + bar(3)"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|expr
operator|.
name|evaluate
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|,
name|DELTA
argument_list|)
expr_stmt|;
block|}
DECL|method|bogusReturnType
specifier|public
specifier|static
name|String
name|bogusReturnType
parameter_list|()
block|{
return|return
literal|"bogus!"
return|;
block|}
comment|/** wrong return type: must be double */
DECL|method|testWrongReturnType
specifier|public
name|void
name|testWrongReturnType
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"bogusReturnType"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo()"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"does not return a double"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|bogusParameterType
specifier|public
specifier|static
name|double
name|bogusParameterType
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
comment|/** wrong param type: must be doubles */
DECL|method|testWrongParameterType
specifier|public
name|void
name|testWrongParameterType
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"bogusParameterType"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo(2)"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"must take only double parameters"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|nonStaticMethod
specifier|public
name|double
name|nonStaticMethod
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/** wrong modifiers: must be static */
DECL|method|testWrongNotStatic
specifier|public
name|void
name|testWrongNotStatic
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"nonStaticMethod"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo()"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is not static"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|nonPublicMethod
specifier|static
name|double
name|nonPublicMethod
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/** wrong modifiers: must be public */
DECL|method|testWrongNotPublic
specifier|public
name|void
name|testWrongNotPublic
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getDeclaredMethod
argument_list|(
literal|"nonPublicMethod"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo()"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is not public"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|NestedNotPublic
specifier|static
class|class
name|NestedNotPublic
block|{
DECL|method|method
specifier|public
specifier|static
name|double
name|method
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/** wrong class modifiers: class containing method is not public */
DECL|method|testWrongNestedNotPublic
specifier|public
name|void
name|testWrongNestedNotPublic
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
name|functions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
name|functions
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
name|NestedNotPublic
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"method"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|JavascriptCompiler
operator|.
name|compile
argument_list|(
literal|"foo()"
argument_list|,
name|functions
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"is not public"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
