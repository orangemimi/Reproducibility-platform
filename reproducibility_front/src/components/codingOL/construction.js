// import { python } from "@codemirror/lang-python";
// import { oneDark } from "@codemirror/theme-one-dark";

export class CMData {
    constructor() {
        // this.themes = { oneDark };
        // this.languages = { python: python() };
        this.CMConfig = {
            disabled: false,
            indentWithTab: true,
            tabSize: 4,
            autofocus: true,
            placeholder: 'input...',
            backgroundColor: 'red',
            language: 'python',
            theme: 'oneDark',
            phrases: 'en-us',
        }
    }
}

export class projectData {
    constructor() {
        this.projectName = ''
        this.projectDirectory = []
        this.defaultProps = {
            label: 'label',
            children: 'children',
        }
        this.currentnode = ''
        this.isrunning = false
        this.isdownloading = false
        this.containerStatus = false
    }
}

export class CMConfigInt {
    constructor() {
        this.disabled = false
        this.indentWithTab = false
        this.tabSize = 4
        this.autofocus = true
        this.placeholder = ''
        this.backgroundColor = ''
        this.language = ''
        this.theme = ''
        this.phrases = ''
    }
}

export class Tree {
    constructor() {
        this.id = ''
        this.label = ''
        this.type = ''
        this.filePath = ''
        this.children = []
    }
}

export class optionItemInt {
    constructor() {
        this.value = ''
        this.label = ''
    }
}

export class tabItemInt {
    constructor() {
        this.title = ''
        this.name = ''
    }
}

export class CodeData {
    constructor() {
        this.codeMap = new Map()
        this.code = "print('hello world')"
    }
}

export class TabData {
    constructor() {
        this.tabIndex = 0
        this.editableTabs = []
        this.editableTabsValue = ''
    }
}

export class menuContextData {
    constructor() {
        this.foldervisible = false
        this.filevisible = false
    }
}

export class FolderCreateData {
    constructor() {
        this.folderCreateDialogShow = false
        this.folderName = ''
    }
}

export class FileCreateData {
    constructor() {
        this.fileCreateDialogShow = false
        this.fileName = ''
    }
}

export class NodeContextData {
    constructor() {
        this.nodeElement = null
        this.nodeData = null
        this.nodeEvent = null
        this.currentNodeFolder = ''
        this.currentNodeFile = ''
        this.currentNodeId = ''
    }
}

export class ProjectDeleteData {
    constructor() {
        this.projectDeleteDialogShow = false
    }
}

export class ProjectCreateData {
    constructor() {
        this.projectCreateDialogShow = false
        this.projectName = ''
        this.pythonOptions = [
            {
                value: 'python:3.9.16',
                label: 'python39',
            },
            {
                value: 'python:3.8',
                label: 'python38',
            },
            {
                value: 'python:3.6',
                label: 'python36',
            },
            {
                value: 'python2.7',
                label: 'python27',
            },
        ]
        this.pythonValue = ''
    }
}

export class Project {
    constructor() {
        this.projectName = ''
        this.userId = ''
        this.pythonVersion = 'python:3.9.16'
        this.workspace = ''
    }
}

export class WorkSpace {
    constructor() {
        this.userName = ''
        this.projectOption = []
        this.projectSelId = ''
        this.LastId = ''
        this.loading = false
    }
}

export class Createfile {
    constructor() {
        this.fileName = ''
        this.filePath = ''
        this.isFolder = false
        this.projectName = ''
        this.userId = ''
        this.parentId = ''
    }
}

export class Deletefile {
}

export class FileRenameData {
    constructor() {
        this.fileRenameDialogShow = false
        this.fileName = ''
    }
}

export class OutputData {
    constructor() {
        this.output = ''
    }
}
