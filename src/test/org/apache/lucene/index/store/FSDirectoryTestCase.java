begin_unit
begin_package
DECL|package|org.apache.lucene.index.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|store
package|;
end_package
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|store
operator|.
name|FSDirectory
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
begin_class
DECL|class|FSDirectoryTestCase
specifier|abstract
specifier|public
class|class
name|FSDirectoryTestCase
extends|extends
name|TestCase
block|{
DECL|field|directory
specifier|private
name|FSDirectory
name|directory
decl_stmt|;
DECL|method|getDirectory
specifier|protected
specifier|final
name|FSDirectory
name|getDirectory
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getDirectory
argument_list|(
literal|false
argument_list|)
return|;
block|}
DECL|method|getDirectory
specifier|protected
specifier|final
name|FSDirectory
name|getDirectory
parameter_list|(
name|boolean
name|create
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|directory
operator|==
literal|null
condition|)
block|{
name|directory
operator|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.index.dir"
argument_list|)
argument_list|,
name|create
argument_list|)
expr_stmt|;
block|}
return|return
name|directory
return|;
block|}
block|}
end_class
end_unit
