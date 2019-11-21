package su.arq.arqviewer.build

interface BuildListProvider {

    fun startLoadingList()

    var onBuildListLoaded: ((builds: BuildListData) -> Unit)?
    fun setOnBuildListLoadedListener( l: ((builds: BuildListData) -> Unit)){
        onBuildListLoaded = l
    }
    var onBuildListLoadingError: ((message: String) -> Unit)?
    fun setOnBuildListLoadingErrorListener(l: ((message: String) -> Unit)){
        onBuildListLoadingError = l
    }
}