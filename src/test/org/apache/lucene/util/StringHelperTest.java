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
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import
begin_class
DECL|class|StringHelperTest
specifier|public
class|class
name|StringHelperTest
extends|extends
name|TestCase
block|{
DECL|method|StringHelperTest
specifier|public
name|StringHelperTest
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp
specifier|protected
name|void
name|setUp
parameter_list|()
block|{   }
DECL|method|tearDown
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{    }
DECL|method|testStringDifference
specifier|public
name|void
name|testStringDifference
parameter_list|()
block|{
name|String
name|test1
init|=
literal|"test"
decl_stmt|;
name|String
name|test2
init|=
literal|"testing"
decl_stmt|;
name|int
name|result
init|=
name|StringHelper
operator|.
name|stringDifference
argument_list|(
name|test1
argument_list|,
name|test2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|==
literal|4
argument_list|)
expr_stmt|;
name|test2
operator|=
literal|"foo"
expr_stmt|;
name|result
operator|=
name|StringHelper
operator|.
name|stringDifference
argument_list|(
name|test1
argument_list|,
name|test2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|==
literal|0
argument_list|)
expr_stmt|;
name|test2
operator|=
literal|"test"
expr_stmt|;
name|result
operator|=
name|StringHelper
operator|.
name|stringDifference
argument_list|(
name|test1
argument_list|,
name|test2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|==
literal|4
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
